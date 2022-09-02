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
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN

open class SmartLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent {

    protected open var childView: View? = null
    protected open val scroller = OverScroller(context)
    protected open var enableDrag = true
    protected open var enableFoldModel = true
    protected open var curState = STATE_HIDDEN
    protected open var collapsedHeight = dp2pxi(180)
    protected open var expandHeight = LayoutParams.WRAP_CONTENT
    protected open var peekHeight = dp2pxi(40)
    protected open var tracker: VelocityTracker? = null
    protected open var maxY = 0
    protected open var minY = 0
    protected open var lastX = 0f
    protected open var lastY = 0f
    protected open var isScrollUp = false

    init {
        setBackgroundColor(Color.YELLOW)
    }

    companion object State {
        const val STATE_COLLAPSED = 1
        const val STATE_EXPANDED = 2
        const val STATE_DRAGGING = 3
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        childView = child
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (enableDrag && enableFoldModel) {
            maxY = childView?.measuredHeight ?: 0
            minY = peekHeight
            childView?.layout(
                paddingStart, measuredHeight - peekHeight, measuredWidth - paddingEnd,
                measuredHeight + maxY
            )
            when (curState) {
                STATE_COLLAPSED -> scrollTo(scrollX, collapsedHeight)
            }
        } else {
            childView?.layout(
                paddingStart, paddingTop, measuredWidth - paddingEnd, measuredHeight - paddingBottom
            )
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
                    collapsed()
                } else {
                    finishScroll()
                }

                tracker!!.clear()
                tracker!!.recycle()
            }
        }
        return true
    }

    protected open fun collapsed() {
        smoothScroll(minY - scrollY)
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
                        scrollY > ((maxY - minY) - collapsedHeight) / 3 + collapsedHeight -> {
                            dy = maxY - minY - scrollY
                        }
                        scrollY > collapsedHeight / 3f -> {
                            dy = collapsedHeight - scrollY
                        }
                    }
                } else {
                    when {
                        scrollY > ((maxY - minY) - collapsedHeight) * 2f / 3 + collapsedHeight -> {
                            dy = maxY - minY - scrollY
                        }
                        scrollY > collapsedHeight * 2f / 3 -> {
                            dy = collapsedHeight - scrollY
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

    // override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
    //     return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && enableDrag
    // }
    //
    // override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
    //     scroller.abortAnimation()
    // }
    //
    // override fun onStopNestedScroll(target: View) {
    //     finishScroll()
    // }
    //
    // override fun onNestedScroll(
    //     target: View,
    //     dxConsumed: Int,
    //     dyConsumed: Int,
    //     dxUnconsumed: Int,
    //     dyUnconsumed: Int
    // ) {
    //     scrollTo(scrollX, scrollY + dyUnconsumed)
    // }
    //
    // override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    //     if (dy > 0) {
    //         val newY = scrollY + dy
    //         if (newY < maxY) {
    //             consumed[1] = dy
    //         }
    //         scrollTo(scrollX, newY)
    //     }
    // }
    //
    // override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
    //     // return scrollY < maxY
    //     return false
    // }
    //
    // override fun onNestedFling(
    //     target: View,
    //     velocityX: Float,
    //     velocityY: Float,
    //     consumed: Boolean
    // ): Boolean {
    //     val isDragging = scrollY in (minY + 1) until maxY
    //     if (isDragging && velocityY < -1500 && !enableFoldModel) {
    //         post {
    //             scroller.startScroll(
    //                 scrollX, scrollY, 0, minY - scrollY, 300
    //             )
    //             ViewCompat.postInvalidateOnAnimation(this)
    //         }
    //     }
    //     return false
    // }
    //


    private fun dp2pxi(dp: Float): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()
    private fun dp2pxi(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()
}