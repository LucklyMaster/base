package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.master.lib.ext.Log
import com.master.lib.ext.dp2pxi
import com.master.lib.ext.screenHeight

open class SmartLayout2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    protected open var childView: View? = null

    /**
     * 是否启用拖拽
     */
    protected open var enableDrag = true

    /**
     * 是否启用折叠模式，不启用则没有半展开状态
     */
    protected open var enableFoldModel = false

    protected open var expandHeight = screenHeight / 2

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
    protected open var curState = STATE_FOLD
    protected open var minY = 0f
    protected open var maxY = 0f

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
        val height = MeasureSpec.makeMeasureSpec(expandHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        minY = y
        maxY = minY + measuredHeight - peekHeight

        childView?.layout(
            paddingStart, paddingTop, measuredWidth - paddingEnd, measuredHeight - paddingBottom
        )
        setState(curState)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    open fun setState(state: Int) {
        if (!enableFoldModel && state == SmartLayout.STATE_EXPAND_HALF) {
            return
        }
        when (state) {
            SmartLayout.STATE_FOLD -> smoothScroll(maxY - y)
            // SmartLayout.STATE_EXPAND_HALF -> smoothScroll(halfExpandHeight - peekHeight - scrollY)
            // SmartLayout.STATE_EXPAND -> smoothScroll(maxY)
            // else -> smoothScroll(-scrollY)
        }
    }

    private fun smoothScroll(dy: Float) {
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
        if (dy > 0) {
            if (y > minY) {
                consumed[1] = dy
            }
            y -= dy
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        Log.d("onNestedScroll")
    }
}