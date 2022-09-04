package com.master.lib.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import com.master.lib.ext.dp2pxi

open class SmartLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent {

    protected open var childView: View? = null
    protected open val scroller = OverScroller(context)
    protected open var tracker: VelocityTracker? = null

    /**
     * 是否启用拖拽
     */
    protected open var enableDrag = true

    /**
     * 是否启用折叠模式，不启用则没有半展开状态
     */
    protected open var enableFoldModel = false

    /**
     * 半展开的高度
     */
    protected open var halfExpandHeight = dp2pxi(180)

    /**
     * 折叠后的peek高度
     */
    protected open var peekHeight = dp2pxi(50)

    /**
     * 当前的状态
     */
    protected open var curState = -1
    protected open var maxY = 0
    protected open var minY = 0
    protected open var lastX = 0f
    protected open var lastY = 0f
    protected open var isScrollUp = false

    init {
        setBackgroundColor(Color.YELLOW)
    }

    companion object State {
        const val STATE_FOLD = 1
        const val STATE_EXPAND_HALF = 2
        const val STATE_EXPAND = 3
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        childView = child
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (enableDrag) {
            maxY = childView?.measuredHeight ?: 0
            minY = peekHeight
            childView?.layout(
                paddingStart, measuredHeight - peekHeight, measuredWidth - paddingEnd,
                measuredHeight + maxY
            )
            setState(STATE_EXPAND_HALF)
        } else {
            childView?.layout(
                paddingStart, paddingTop, measuredWidth - paddingEnd, measuredHeight - paddingBottom
            )
        }
    }

    open fun setState(state: Int) {
        if (curState == state) {
            return
        }
        if (!enableFoldModel && state == STATE_EXPAND_HALF) {
            return
        }
        curState = state
        when (curState) {
            STATE_FOLD -> smoothScroll(-scrollY)
            STATE_EXPAND_HALF -> smoothScroll(halfExpandHeight - peekHeight - scrollY)
            STATE_EXPAND -> smoothScroll(maxY)
            else -> smoothScroll(-scrollY)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (scroller.computeScrollOffset() || !enableDrag) {
            lastX = 0f
            lastY = 0f
            return false
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                tracker = VelocityTracker.obtain()
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                tracker!!.addMovement(event)
                tracker!!.computeCurrentVelocity(1000)

                val dy = event.y - lastY
                scrollTo(scrollX, (scrollY - dy).toInt())
                lastY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (tracker!!.yVelocity > 1500 && !enableFoldModel) {
                    setState(STATE_FOLD)
                } else {
                    finishScroll()
                }
                dispatchNestedPreFling(tracker!!.xVelocity, tracker!!.yVelocity)
                tracker!!.clear()
                tracker!!.recycle()
            }
        }
        return true
    }

    protected open fun smoothScroll(dy: Int) {
        scroller.startScroll(scrollX, scrollY, 0, dy)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    /**
     * 结束滚动，根据当前的滚动距离将View滚动到合适的位置；
     * 每个阶段以1/3为界限，超过1/3就滚动到下一阶段
     */
    protected open fun finishScroll() {
        if (enableDrag) {
            val threshold = if (isScrollUp) (maxY - minY) / 3 else (maxY - minY) * 2 / 3
            var dy = (if (scrollY > threshold) maxY - minY else 0) - scrollY
            if (enableFoldModel) {
                if (isScrollUp) {
                    when {
                        scrollY > ((maxY - minY) - halfExpandHeight) / 3 + halfExpandHeight -> {
                            dy = maxY - minY - scrollY
                        }
                        scrollY > halfExpandHeight / 3f -> {
                            dy = halfExpandHeight - peekHeight - scrollY
                        }
                    }
                } else {
                    when {
                        scrollY > ((maxY - minY) - halfExpandHeight) * 2f / 3 + halfExpandHeight -> {
                            dy = maxY - minY - scrollY
                        }
                        scrollY > halfExpandHeight * 2f / 3 -> {
                            dy = halfExpandHeight - peekHeight - scrollY
                        }
                    }
                }
            }
            scroller.startScroll(scrollX, scrollY, 0, dy, 300)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        var newY = if (y > (maxY - minY)) maxY - minY else y
        newY = if (newY < 0) 0 else newY
        isScrollUp = y > scrollY
        super.scrollTo(x, newY)
    }

    /**
     * 决定是否配合子View进行嵌套滑动
     * @param child 当前View的直接子View
     * @param target 启动嵌套滚动的子View
     * @param nestedScrollAxes 滑动方向
     * @return Boolean
     */
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && enableDrag
    }

    /**
     * 对嵌套滚动操作的成功声明做出反应。该方法会在 [onStartNestedScroll] 返回 true 后被调用。
     * @param child View
     * @param target View
     * @param nestedScrollAxes Int
     */
    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        scroller.abortAnimation()
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (dy > 0) {
            val newY = scrollY + dy
            if (newY + peekHeight < maxY) {
                consumed[1] = dy
            }
            scrollTo(scrollX, newY)
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        scrollTo(scrollX, scrollY + dyUnconsumed)
    }

    override fun onStopNestedScroll(target: View) {
        finishScroll()
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        if (scrollY + peekHeight < maxY) {
            if (velocityY > 1500) {
                if (curState == STATE_EXPAND_HALF) {
                    // smoothScroll(maxY - peekHeight - scrollY)
                    // smoothScroll((velocityY * 0.25f).toInt())
                }
            }
            return true
        }
        return false
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        val isDragging = scrollY in (minY + 1) until maxY
        if (isDragging && velocityY < -1500 && !enableFoldModel) {
            post {
                scroller.startScroll(
                    scrollX, scrollY, 0, minY - scrollY, 300
                )
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
        return false
    }
}