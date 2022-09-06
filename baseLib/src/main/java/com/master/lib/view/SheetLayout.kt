package com.master.lib.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import com.master.lib.ext.dp2pxi
import com.master.lib.ext.screenHeight

/**
 * 可以用作抽屉的Layout,只能包含一个直接子View
 * @author: MasterChan
 * @date: 2022-09-06 13:33
 */
open class SheetLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent {

    protected var childView: View? = null

    /**
     * 是否启用拖拽
     */
    protected var enableDrag = true

    /**
     * 是否启用折叠模式，不启用则没有半展开状态
     */
    protected var enableFoldModel = true

    /**
     * 展开后的高度
     */
    protected var expandHeight = screenHeight / 2

    /**
     * 半展开的高度
     */
    protected var halfExpandHeight = expandHeight / 2

    /**
     * 折叠后的peek高度
     */
    protected var peekHeight = context.dp2pxi(50)

    /**
     * 当前的状态
     */
    protected open var curState = STATE_FOLD
    protected open var isScrollUp = false
    protected open var minY = 0f
    protected open var maxY = 0f
    protected open var lastY = 0f
    protected open var tracker: VelocityTracker? = null

    companion object State {
        const val STATE_FOLD = 1
        const val STATE_EXPAND_HALF = 2
        const val STATE_EXPAND = 3
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        childView = child
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = if (expandHeight > 0) {
            MeasureSpec.makeMeasureSpec(expandHeight, MeasureSpec.EXACTLY)
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        minY = y
        maxY = minY + measuredHeight - peekHeight

        childView?.layout(
            paddingStart, paddingTop, measuredWidth - paddingEnd, measuredHeight - paddingBottom
        )
        setStateInternal(curState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
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
                if (tracker!!.yVelocity > 1500 && !enableFoldModel) {
                    setStateInternal(STATE_FOLD)
                } else if (tracker!!.yVelocity < -1500 && !enableFoldModel) {
                    setStateInternal(STATE_EXPAND)
                } else {
                    finishScroll()
                }
                tracker!!.clear()
                tracker!!.recycle()
            }
        }
        return true
    }

    open fun finishScroll() {
        val threshold = if (isScrollUp) height * 2 / 3 else height / 3
        var dy = if (y > minY + threshold) maxY - y else minY - y
        if (enableFoldModel) {
            if (isScrollUp) {
                when {
                    y < minY + (height - halfExpandHeight) * 2 / 3 -> {
                        dy = minY - y
                    }
                    y < minY + (height - halfExpandHeight) + halfExpandHeight * 2 / 3 -> {
                        dy = minY + (height - halfExpandHeight) - y
                    }
                }
            } else {
                when {
                    y > minY + (height - halfExpandHeight) + halfExpandHeight / 3 -> {
                        dy = maxY - y
                    }
                    y > minY + (height - halfExpandHeight) / 3 -> {
                        dy = minY + (height - halfExpandHeight).toFloat() - y
                    }
                }
            }
        }
        smoothScroll(dy)
    }

    open fun setState(state: Int) = apply {
        if (curState == state) {
            return@apply
        }
        curState = state
        invalidate()
    }

    open fun setPeekHeight(peekHeight: Int) = apply {
        this.peekHeight = peekHeight
    }

    open fun setExpandHeight(expandHeight: Int) = apply {
        this.expandHeight = expandHeight
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

    protected open fun setStateInternal(state: Int) {
        if (!enableFoldModel && state == STATE_EXPAND_HALF) {
            return
        }
        when (state) {
            STATE_FOLD -> smoothScroll(maxY - y)
            STATE_EXPAND_HALF -> smoothScroll(minY + halfExpandHeight - y)
            STATE_EXPAND -> smoothScroll(minY - y)
            else -> smoothScroll(maxY - y)
        }
    }

    private fun smoothScroll(dy: Float) {
        isScrollUp = dy < 0
        y += dy
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

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
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
}