package com.master.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * RecyclerView的分割线
 * @author MasterChan
 * @date 2021-12-24 13:41
 */
class RecyclerViewDivider : ItemDecoration {

    private var mPaint: Paint? = null
    private var mDrawable: Drawable? = null
    private var mDividerHeight = 2
    private var mOrientation = 0

    companion object Orientation {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        const val BOTH = 2
    }

    constructor(context: Context, orientation: Int = HORIZONTAL) {
        setOrientation(orientation)
        val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        mDrawable = a.getDrawable(0)
        a.recycle()
    }

    constructor(context: Context, orientation: Int = HORIZONTAL, drawableId: Int) {
        setOrientation(orientation)
        mDrawable = AppCompatResources.getDrawable(context, drawableId)
        mDividerHeight = mDrawable!!.intrinsicHeight
    }

    constructor(
        orientation: Int = HORIZONTAL, dividerHeight: Int = 2,
        @ColorInt dividerColor: Int = Color.parseColor("#D6D6D6")
    ) {
        setOrientation(orientation)
        mDividerHeight = dividerHeight
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = dividerColor
        mPaint!!.style = Paint.Style.FILL
    }

    private fun setOrientation(orientation: Int) {
        require(!(orientation < 0 || orientation > 2)) { "invalid orientation" }
        mOrientation = orientation
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutParams = view.layoutParams as RecyclerView.LayoutParams
        val itemPosition = layoutParams.viewLayoutPosition
        var childCount = parent.adapter?.itemCount ?: 0
        when (mOrientation) {
            HORIZONTAL -> {
                childCount -= 1
                //最后一行不绘制
                outRect.set(0, 0, 0, if (itemPosition != childCount) mDividerHeight else 0)
            }
            VERTICAL -> {
                childCount -= 1
                //最后一列不绘制
                outRect.set(0, 0, if (itemPosition != childCount) mDividerHeight else 0, 0)
            }
            BOTH -> {
                val spanCount = getSpanCount(parent)
                val offset = spanCount.minus(1).times(mDividerHeight).div(spanCount)
                val left = itemPosition % spanCount * (mDividerHeight - offset)
                val right = offset - left
                when {
                    // 如果是最后一行，不绘制底部
                    isLastRow(parent, itemPosition, spanCount, childCount) -> {
                        outRect.set(left, 0, right, 0)
                    }
                    // 如果是最后一列，不绘制右边
                    isLastColumn(parent, itemPosition, spanCount, childCount) -> {
                        outRect.set(left, 0, right, mDividerHeight)
                    }
                    else -> {
                        outRect.set(left, 0, right, mDividerHeight)
                    }
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        when (mOrientation) {
            HORIZONTAL -> {
                drawHorizontal(c, parent)
            }
            VERTICAL -> {
                drawVertical(c, parent)
            }
            BOTH -> {
                drawHorizontal(c, parent)
                drawVertical(c, parent)
            }
        }
    }

    /**
     * 绘制横向的分割线
     * @param canvas Canvas
     * @param parent RecyclerView
     */
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left
            val right = child.right
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + mDividerHeight
            mDrawable?.let {
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }
            mPaint?.let { canvas.drawRect(Rect(left, top, right, bottom), it) }
        }
    }

    /**
     * 绘制竖向的分割线
     * @param canvas Canvas
     * @param parent RecyclerView
     */
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + layoutParams.rightMargin
            val right = left + mDividerHeight
            val top = child.top
            val bottom = child.bottom + mDividerHeight
            mDrawable?.let {
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }
            mPaint?.let { canvas.drawRect(Rect(left, top, right, bottom), it) }
        }
    }

    /**
     * 获取当RecyclerView的layoutManager是[GridLayoutManager]或[StaggeredGridLayoutManager]
     * 时的spanCount
     * @param parent RecyclerView
     * @return Int
     */
    private fun getSpanCount(parent: RecyclerView): Int {
        return when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> layoutManager.spanCount
            is StaggeredGridLayoutManager -> layoutManager.spanCount
            else -> -1
        }
    }

    /**
     * 判断当RecyclerView的layoutManager是[GridLayoutManager]或[StaggeredGridLayoutManager]
     * 时，当前的item是否是最后一列
     * @param parent RecyclerView
     * @param itemPosition ItemView的位置
     * @param spanCount 一行有几列
     * @param count ItemView的数量
     * @return Boolean
     */
    private fun isLastColumn(
        parent: RecyclerView, itemPosition: Int, spanCount: Int, count: Int
    ): Boolean {
        var childCount = count
        return when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                val orientation = layoutManager.orientation
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    (itemPosition + 1) % spanCount == 0
                } else {
                    if (childCount == spanCount) {
                        true
                    } else {
                        childCount -= childCount % spanCount
                        itemPosition >= childCount
                    }
                }
            }
            is StaggeredGridLayoutManager -> {
                val orientation = layoutManager.orientation
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    (itemPosition + 1) % spanCount == 0
                } else {
                    if (childCount == spanCount) {
                        true
                    } else {
                        childCount -= childCount % spanCount
                        itemPosition >= childCount
                    }
                }
            }
            else -> false
        }
    }

    /**
     * 判断当RecyclerView的layoutManager是[GridLayoutManager]或[StaggeredGridLayoutManager]
     * 时，当前的item是否是最后一行
     * @param parent RecyclerView
     * @param itemPosition ItemView的位置
     * @param spanCount 一行有几列
     * @param count ItemView的数量
     * @return Boolean
     */
    private fun isLastRow(
        parent: RecyclerView, itemPosition: Int, spanCount: Int, count: Int
    ): Boolean {
        var childCount = count
        return when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                val orientation = layoutManager.orientation
                if (orientation == GridLayoutManager.VERTICAL) {
                    if (childCount == spanCount) {
                        true
                    } else {
                        childCount -= childCount % spanCount
                        itemPosition >= childCount
                    }
                } else {
                    (itemPosition + 1) % spanCount == 0
                }
            }
            is StaggeredGridLayoutManager -> {
                val orientation = layoutManager.orientation
                if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                    if (childCount == spanCount) {
                        true
                    } else {
                        childCount -= childCount % spanCount
                        itemPosition >= childCount
                    }
                } else {
                    (itemPosition + 1) % spanCount == 0
                }
            }
            else -> false
        }
    }
}