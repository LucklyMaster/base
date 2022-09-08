package com.master.lib.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import com.master.lib.R
import com.master.lib.ext.dp2pxi
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
     * 展开后高度相对于屏幕高度的比率，与[expandHeight]相比，优先级更高
     */
    var expandHeightRatio = 0.65f

    /**
     * 半展开的高度
     */
    var halfExpandHeight = expandHeight / 2

    /**
     * 折叠后的peek高度
     */
    var peekHeight = context.dp2pxi(50)

    /**
     * 当前的状态
     */
    var curState = STATE_FOLD

    /**
     * 动画执行的速度(px/ms)，路程/时间
     */
    var animatorSpeed = 950f / 300

    protected var smoothAnimator: ValueAnimator? = null
    protected var stateChangedListener: OnStateChangedListener? = null
    protected var isLayout = false
    protected var isScrollUp = false
    protected var minY = 0f
    protected var maxY = 0f
    protected var lastY = 0f
    protected var tracker: VelocityTracker? = null

    companion object State {
        const val STATE_FOLD = 1
        const val STATE_EXPAND_HALF = 2
        const val STATE_EXPAND = 3
    }

    fun interface OnStateChangedListener {
        fun onChanged(state: Int)
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
        expandHeightRatio = a.getFloat(
            R.styleable.SheetLayout_mc_expandHeightRatio, expandHeightRatio
        )
        halfExpandHeight = a.getDimensionPixelOffset(
            R.styleable.SheetLayout_mc_halfExpandHeight, halfExpandHeight
        )
        peekHeight = a.getDimensionPixelOffset(R.styleable.SheetLayout_mc_peekHeight, peekHeight)
        animatorSpeed = a.getFloat(R.styleable.SheetLayout_mc_animatorSpeed, animatorSpeed)
        curState = a.getInt(R.styleable.SheetLayout_mc_initialState, curState)

        a.recycle()
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        childView = child
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val realHeight = when {
            MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY -> {
                heightMeasureSpec
            }
            expandHeightRatio > 0 -> {
                MeasureSpec.makeMeasureSpec(
                    (expandHeightRatio * context.screenHeight).toInt(), MeasureSpec.EXACTLY
                )
            }
            expandHeight > 0 -> {
                MeasureSpec.makeMeasureSpec(expandHeight, MeasureSpec.EXACTLY)
            }
            else -> {
                heightMeasureSpec
            }
        }
        super.onMeasure(widthMeasureSpec, realHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isLayout) {
            return
        }
        minY = y
        maxY = minY + measuredHeight - peekHeight

        childView?.layout(
            paddingStart, paddingTop, measuredWidth - paddingEnd, measuredHeight - paddingBottom
        )
        setStateInternal(curState, withAnimator = false, isFromUser = false)
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
                smoothScroll(dy)
                lastY = event.rawY
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (tracker!!.yVelocity > 1000 && !enableFoldModel) {
                    setStateInternal(STATE_FOLD, true)
                } else if (tracker!!.yVelocity < -1000 && !enableFoldModel) {
                    setStateInternal(STATE_EXPAND, true)
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
                    y < minY + (height - halfExpandHeight) * 2 / 3 -> {
                        setStateInternal(STATE_EXPAND, true)
                    }
                    y < minY + (height - halfExpandHeight) + halfExpandHeight * 2 / 3 -> {
                        setStateInternal(STATE_EXPAND_HALF, true)
                    }
                    else -> {
                        setStateInternal(STATE_FOLD, true)
                    }
                }
            } else {
                when {
                    y > minY + (height - halfExpandHeight) + halfExpandHeight / 3 -> {
                        setStateInternal(STATE_FOLD, true)
                    }
                    y > minY + (height - halfExpandHeight) / 3 -> {
                        setStateInternal(STATE_EXPAND_HALF, true)
                    }
                    else -> {
                        setStateInternal(STATE_EXPAND, true)
                    }
                }
            }
        } else {
            val threshold = if (isScrollUp) height * 2 / 3 else height / 3
            if (y > minY + threshold) {
                setStateInternal(STATE_FOLD, true)
            } else {
                setStateInternal(STATE_EXPAND, true)
            }
        }
    }

    protected open fun setStateInternal(
        state: Int,
        withAnimator: Boolean = false,
        isFromUser: Boolean = true
    ) {
        if (!enableFoldModel && state == STATE_EXPAND_HALF) {
            return
        }
        when (state) {
            STATE_FOLD -> smoothScroll(maxY - y, withAnimator)
            STATE_EXPAND_HALF -> smoothScroll(minY + (height - halfExpandHeight) - y, withAnimator)
            STATE_EXPAND -> smoothScroll(minY - y, withAnimator)
            else -> smoothScroll(maxY - y, withAnimator)
        }
        if (curState != state) {
            stateChangedListener?.onChanged(state)
        }
        curState = state
    }

    protected open fun smoothScroll(dy: Float, withAnimator: Boolean = false) {
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
            smoothAnimator!!.start()
        } else {
            y += dy
        }
    }

    override fun setY(y: Float) {
        var newY = if (y > maxY) maxY else y
        newY = if (newY < minY) minY else newY
        super.setY(newY)
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
            smoothScroll(-dy.toFloat())
        }
        if (y == minY && !target.canScrollVertically(-1) && !isScrollUp) {
            consumed[1] = dy
            smoothScroll(-dy.toFloat())
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

    open fun setState(state: Int, withAnimator: Boolean = true) = apply {
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

    open fun setExpandHeightRatio(expandHeightRatio: Float) = apply {
        this.expandHeightRatio = expandHeightRatio
    }

    open fun setHalfExpandHeight(halfExpandHeight: Int) = apply {
        this.halfExpandHeight = halfExpandHeight
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

    open fun setOnStateChangedListener(listener: OnStateChangedListener) = apply {
        stateChangedListener = listener
    }
}