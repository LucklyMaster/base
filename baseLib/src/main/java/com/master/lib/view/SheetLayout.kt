package com.master.lib.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import com.master.lib.R
import com.master.lib.enums.SheetState
import com.master.lib.ext.screenHeight
import kotlin.math.abs

/**
 * 可以用作抽屉的Layout,只能包含一个直接子View
 * @author: MasterChan
 * @date: 2022-09-06 13:33
 */
open class SheetLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes), NestedScrollingParent {

    protected var childView: View? = null

    /**
     * 是否启用拖拽
     */
    var enableDrag = true

    /**
     * 是否启用折叠模式，不启用则没有半展开状态
     */
    var enableFoldModel = true

    /**
     * 展开后的高度
     */
    var expandHeight = context.screenHeight / 2

    /**
     * 半展开的高度
     */
    var displayHeight = expandHeight / 2

    /**
     * 折叠后的peek高度
     */
    var peekHeight = 0

    /**
     * 当前的状态
     */
    var curState = SheetState.FOLD

    /**
     * 动画执行的速度(px/ms)，路程/时间
     */
    var animatorSpeed = 950f / 300

    protected var smoothAnimator: ValueAnimator? = null
    protected var stateChangedListeners = mutableListOf<OnStateChangedListener>()
    protected var scrollListeners = mutableListOf<OnScrollListener>()
    protected var isLayout = false
    protected var isScrollUp = false
    protected var minY = 0f
    protected var maxY = 0f
    protected var lastY = 0f
    protected var tracker: VelocityTracker? = null

    fun interface OnStateChangedListener {
        fun onChanged(state: SheetState)
    }

    fun interface OnScrollListener {
        fun onScroll(dy: Float)
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SheetLayout, defStyleAttr, defStyleRes
        )

        enableDrag = a.getBoolean(R.styleable.SheetLayout_mc_enableDrag, enableDrag)
        enableFoldModel = a.getBoolean(R.styleable.SheetLayout_mc_enableFoldModel, enableFoldModel)
        expandHeight = a.getDimensionPixelOffset(
            R.styleable.SheetLayout_mc_expandHeight, expandHeight
        )
        displayHeight = a.getDimensionPixelOffset(
            R.styleable.SheetLayout_mc_displayHeight, displayHeight
        )
        peekHeight = a.getDimensionPixelOffset(R.styleable.SheetLayout_mc_peekHeight, peekHeight)
        animatorSpeed = a.getFloat(R.styleable.SheetLayout_mc_animatorSpeed, animatorSpeed)
        curState = SheetState.convert2State(
            a.getInt(R.styleable.SheetLayout_mc_sheetState, SheetState.FOLD.ordinal)
        )

        a.recycle()
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        childView = child
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = heightMeasureSpec
        if (expandHeight != 0) {
            height = MeasureSpec.makeMeasureSpec(expandHeight, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!isLayout) {
            minY = y
            maxY = minY + measuredHeight - peekHeight
        }

        childView?.layout(
            paddingStart, paddingTop, measuredWidth - paddingEnd, measuredHeight - paddingBottom
        )

        if (!isLayout) {
            setStateInternal(curState, withAnimator = false, isFromUser = false)
        }

        isLayout = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (smoothAnimator?.isRunning == true || !enableDrag) {
            return false
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                tracker = VelocityTracker.obtain()
                lastY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                tracker!!.addMovement(event)
                tracker!!.computeCurrentVelocity(1000)
                val dy = event.rawY - lastY
                smoothScroll(SheetState.DRAGGING, dy)
                lastY = event.rawY
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (tracker!!.yVelocity > 1000 && !enableFoldModel) {
                    setStateInternal(SheetState.FOLD, true)
                } else if (tracker!!.yVelocity < -1000 && !enableFoldModel) {
                    setStateInternal(SheetState.EXPAND, true)
                } else {
                    finishScroll()
                }
                tracker!!.clear()
                tracker!!.recycle()
            }
        }
        return true
    }

    protected open fun finishScroll() {
        if (enableFoldModel) {
            if (isScrollUp) {
                when {
                    y < minY + (height - displayHeight) * 2 / 3 -> {
                        setStateInternal(SheetState.EXPAND, true)
                    }
                    y < minY + (height - displayHeight) + displayHeight * 2 / 3 -> {
                        setStateInternal(SheetState.DISPLAY, true)
                    }
                    else -> {
                        setStateInternal(SheetState.FOLD, true)
                    }
                }
            } else {
                when {
                    y > minY + (height - displayHeight) + displayHeight / 3 -> {
                        setStateInternal(SheetState.FOLD, true)
                    }
                    y > minY + (height - displayHeight) / 3 -> {
                        setStateInternal(SheetState.DISPLAY, true)
                    }
                    else -> {
                        setStateInternal(SheetState.EXPAND, true)
                    }
                }
            }
        } else {
            val threshold = if (isScrollUp) height * 2 / 3 else height / 3
            if (y > minY + threshold) {
                setStateInternal(SheetState.FOLD, true)
            } else {
                setStateInternal(SheetState.EXPAND, true)
            }
        }
    }

    protected open fun setStateInternal(
        state: SheetState,
        withAnimator: Boolean = false,
        isFromUser: Boolean = true
    ) {
        if (!enableFoldModel && state == SheetState.DISPLAY) {
            return
        }
        when (state) {
            SheetState.DRAGGING -> {}
            SheetState.FOLD -> smoothScroll(state, maxY - y, withAnimator)
            SheetState.DISPLAY -> smoothScroll(
                state, minY + (height - displayHeight) - y, withAnimator
            )
            SheetState.EXPAND -> smoothScroll(state, minY - y, withAnimator)
        }
    }

    protected open fun smoothScroll(state: SheetState, dy: Float, withAnimator: Boolean = false) {
        isScrollUp = dy < 0
        if (withAnimator) {
            if (smoothAnimator?.isRunning == true) {
                smoothAnimator?.cancel()
            }
            val curY = y
            smoothAnimator = ValueAnimator()
            smoothAnimator!!.setFloatValues(0f, dy)
            smoothAnimator!!.duration = (abs(dy) / animatorSpeed).toLong()
            smoothAnimator!!.removeAllUpdateListeners()
            smoothAnimator!!.addUpdateListener {
                y = curY + it.animatedValue as Float
            }
            smoothAnimator!!.doOnEnd {
                if (curState != state) {
                    curState = state
                    stateChangedListeners.forEach { it.onChanged(state) }
                }
            }
            smoothAnimator!!.start()
        } else {
            y += dy
            if (curState != state) {
                curState = state
                stateChangedListeners.forEach { it.onChanged(state) }
            }
        }
    }

    override fun setY(y: Float) {
        val curY = getY()
        var newY = if (y > maxY) maxY else y
        newY = if (newY < minY) minY else newY
        super.setY(newY)
        scrollListeners.forEach { it.onScroll(newY - curY) }
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && enableDrag
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        if (smoothAnimator?.isRunning == true) {
            smoothAnimator?.cancel()
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (y > minY) {
            consumed[1] = dy
            smoothScroll(SheetState.DRAGGING, -dy.toFloat())
        }
        if (y == minY && !target.canScrollVertically(-1) && !isScrollUp) {
            consumed[1] = dy
            smoothScroll(SheetState.DRAGGING, -dy.toFloat())
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        if (y > minY) {
            return true
        }
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    override fun onStopNestedScroll(target: View) {
        finishScroll()
    }

    open fun setState(state: SheetState, withAnimator: Boolean = true) = apply {
        if (curState == state) {
            return@apply
        }
        setStateInternal(state, withAnimator)
    }

    open fun setPeekHeight(peekHeight: Int) = apply {
        this.peekHeight = peekHeight
    }

    open fun setExpandHeight(expandHeight: Int) = apply {
        this.expandHeight = expandHeight
    }

    open fun setDisplayHeight(displayHeight: Int) = apply {
        this.displayHeight = displayHeight
    }

    open fun enableDrag(enableDrag: Boolean) = apply {
        this.enableDrag = enableDrag
    }

    open fun enableFoldModel(enableFoldModel: Boolean) = apply {
        this.enableFoldModel = enableFoldModel
    }

    open fun setAnimatorSpeed(animatorSpeed: Float) = apply {
        this.animatorSpeed = animatorSpeed
    }

    open fun addOnStateChangedListener(listener: OnStateChangedListener) = apply {
        stateChangedListeners.add(listener)
    }

    open fun removeOnStateChangedListener(listener: OnStateChangedListener) = apply {
        stateChangedListeners.remove(listener)
    }

    open fun addOnScrollListener(listener: OnScrollListener) = apply {
        scrollListeners.add(listener)
    }

    open fun removeOnScrollListener(listener: OnScrollListener) = apply {
        scrollListeners.remove(listener)
    }
}