// package com.master.lib.widget
//
// import android.content.Context
// import android.graphics.Canvas
// import android.graphics.Rect
// import android.graphics.drawable.ColorDrawable
// import android.graphics.drawable.Drawable
// import android.view.View
// import androidx.annotation.ColorInt
// import androidx.core.content.ContextCompat
// import androidx.recyclerview.widget.GridLayoutManager
// import androidx.recyclerview.widget.LinearLayoutManager
// import androidx.recyclerview.widget.RecyclerView
// import androidx.recyclerview.widget.StaggeredGridLayoutManager
//
// /**
//  * RecyclerView的分割线
//  * @author MasterChan
//  * @date 2021-12-24 13:41
//  */
//
// open class RecyclerViewDivider2 : RecyclerView.ItemDecoration {
//
//     protected var drawable: Drawable
//     protected var dividerHeight = 0
//     protected var orientation = 0
//         private set
//     protected var isIncludeFirstRow = false
//     protected var isIncludeLastRow = false
//     protected var isIncludeFirstColumn = false
//     protected var isIncludeLastColumn = false
//     protected var dividerMargins = 0f
//
//     companion object Orientation {
//         const val HORIZONTAL = 0
//         const val VERTICAL = 1
//         const val BOTH = 2
//     }
//
//     constructor(context: Context, orientation: Int = HORIZONTAL) {
//         setOrientation(orientation)
//         val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
//         drawable = a.getDrawable(0)!!
//         a.recycle()
//     }
//
//     constructor(context: Context, orientation: Int = HORIZONTAL, drawableId: Int) {
//         setOrientation(orientation)
//         drawable = ContextCompat.getDrawable(context, drawableId)!!
//         dividerHeight = drawable.intrinsicHeight
//     }
//
//     constructor(orientation: Int, dividerHeight: Int, @ColorInt dividerColor: Int) {
//         setOrientation(orientation)
//         this.dividerHeight = dividerHeight
//         drawable = ColorDrawable(dividerColor)
//     }
//
//     private fun setOrientation(orientation: Int) {
//         require(!(orientation < 0 || orientation > 2)) { "invalid orientation" }
//         this.orientation = orientation
//     }
//
//     open fun setIncludeFirst(isIncludeFirst: Boolean) = apply {
//         this.isIncludeFirstRow = isIncludeFirst
//     }
//
//     open fun setIncludeLast(isIncludeLast: Boolean) = apply {
//         this.isIncludeLastRow = isIncludeLast
//     }
//
//     open fun setDividerMargins(margins: Float) = apply {
//         this.dividerMargins = margins
//     }
//
//     override fun getItemOffsets(
//         outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
//     ) {
//         require(parent.adapter != null) { "you need setAdapter first" }
//         val layoutParams = view.layoutParams as RecyclerView.LayoutParams
//         val itemPosition = layoutParams.viewLayoutPosition
//         val childCount = parent.adapter?.itemCount ?: 0
//         val spanCount = getSpanCount(parent)
//         when (orientation) {
//             HORIZONTAL -> {
//                 if (isFirstRow(parent, itemPosition, spanCount, childCount)) {
//
//                 }
//                 if (itemPosition == 0 && isIncludeFirstRow) {
//                     outRect.set(0, dividerHeight, 0, dividerHeight)
//                 }
//                 outRect.set(0, 0, 0, dividerHeight)
//             }
//         }
//         super.getItemOffsets(outRect, view, parent, state)
//     }
//
//     override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//         super.onDraw(c, parent, state)
//         when (orientation) {
//             HORIZONTAL -> {
//                 drawHorizontal(c, parent)
//             }
//             VERTICAL -> {
//                 drawVertical(c, parent)
//             }
//             BOTH -> {
//                 drawHorizontal(c, parent)
//                 drawVertical(c, parent)
//             }
//         }
//     }
//
//     /**
//      * 绘制横向的分割线
//      * @param canvas Canvas
//      * @param parent RecyclerView
//      */
//
//     protected open fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
//         val childSize = parent.childCount
//         for (i in 0 until childSize) {
//             val child = parent.getChildAt(i)
//             val layoutParams = child.layoutParams as RecyclerView.LayoutParams
//             val left = child.left + if (isIncludeFirstRow)
//             val right = child.right
//             val top = child.bottom + layoutParams.bottomMargin
//             val bottom = top + dividerHeight
//             drawable?.let {
//                 it.setBounds(left, top, right, bottom)
//                 it.draw(canvas)
//             }
//             //顶部分割线
//             if (i == 0) {
//                 mPaint?.let {
//                     canvas.drawRect(
//                         Rect(
//                             left, child.top - layoutParams.topMargin - dividerHeight, right,
//                             child.top - layoutParams.topMargin
//                         ), it
//                     )
//                 }
//             }
//             mPaint?.let { canvas.drawRect(Rect(left, top, right, bottom), it) }
//         }
//     }
//
//     /**
//      * 绘制竖向的分割线
//      * @param canvas Canvas
//      * @param parent RecyclerView
//      */
//
//     protected open fun drawVertical(canvas: Canvas, parent: RecyclerView) {
//         val childSize = parent.childCount
//         for (i in 0 until childSize) {
//             val child = parent.getChildAt(i)
//             val layoutParams = child.layoutParams as RecyclerView.LayoutParams
//             val left = child.right + layoutParams.rightMargin
//             val right = left + dividerHeight
//             val top = child.top
//             val bottom = child.bottom + dividerHeight
//             drawable?.let {
//                 it.setBounds(left, top, right, bottom)
//                 it.draw(canvas)
//             }
//             mPaint?.let { canvas.drawRect(Rect(left, top, right, bottom), it) }
//         }
//     }
//
//     /**
//      * 获取当RecyclerView的layoutManager是[GridLayoutManager]或[StaggeredGridLayoutManager]
//      * 时的spanCount
//      * @param parent RecyclerView
//      * @return Int
//      */
//
//     protected open fun getSpanCount(parent: RecyclerView): Int {
//         return when (val layoutManager = parent.layoutManager) {
//             is GridLayoutManager -> layoutManager.spanCount
//             is StaggeredGridLayoutManager -> layoutManager.spanCount
//             is LinearLayoutManager -> if (layoutManager.orientation == RecyclerView.VERTICAL) 1 else layoutManager.childCount
//             else -> -1
//         }
//     }
//
//     // TODO: 重写第一行第一列
//     protected open fun isFirstRow(
//         parent: RecyclerView,
//         itemPosition: Int,
//         spanCount: Int,
//         count: Int
//     ): Boolean {
//         return when (val layoutManager = parent.layoutManager) {
//             is GridLayoutManager -> {
//                 itemPosition < spanCount
//             }
//             is StaggeredGridLayoutManager -> {
//                 itemPosition < spanCount
//             }
//             is LinearLayoutManager -> {
//                 if (layoutManager.orientation == RecyclerView.VERTICAL) {
//                     itemPosition == 0
//                 } else {
//                     //水平滚动时，只有一行，所以所有item都在第一行
//                     true
//                 }
//             }
//             else -> false
//         }
//     }
//
//     protected open fun isFirstColumn(
//         parent: RecyclerView,
//         itemPosition: Int,
//         spanCount: Int,
//         count: Int
//     ): Boolean {
//         return when (val layoutManager = parent.layoutManager) {
//             is GridLayoutManager -> {
//                 itemPosition % spanCount == 0
//             }
//             is StaggeredGridLayoutManager -> {
//                 itemPosition % spanCount == 0
//             }
//             is LinearLayoutManager -> {
//                 if (layoutManager.orientation == RecyclerView.VERTICAL) {
//                     //垂直滚动时，只有一列，所以所有item都在第一列
//                     true
//                 } else {
//                     itemPosition == 0
//                 }
//             }
//             else -> false
//         }
//     }
//
//     /**
//      * 判断当前的item是否是最后一行
//      * @param parent RecyclerView
//      * @param itemPosition ItemView的位置
//      * @param spanCount 一行有几列
//      * @param childCount ItemView的数量
//      * @return Boolean
//      */
//     protected open fun isLastRow(
//         parent: RecyclerView,
//         itemPosition: Int,
//         spanCount: Int,
//         childCount: Int
//     ): Boolean {
//         return when (val layoutManager = parent.layoutManager) {
//             is GridLayoutManager -> {
//                 val orientation = layoutManager.orientation
//                 if (orientation == GridLayoutManager.VERTICAL) {
//                     isInBottomEdge(childCount, spanCount, itemPosition)
//                 } else {
//                     isInRightEdge(spanCount, itemPosition)
//                 }
//             }
//             is StaggeredGridLayoutManager -> {
//                 val orientation = layoutManager.orientation
//                 if (orientation == GridLayoutManager.VERTICAL) {
//                     isInBottomEdge(childCount, spanCount, itemPosition)
//                 } else {
//                     isInRightEdge(spanCount, itemPosition)
//                 }
//             }
//             is LinearLayoutManager -> {
//                 isInBottomEdge(childCount, spanCount, itemPosition)
//             }
//             else -> false
//         }
//     }
//
//     /**
//      * 判断当前的item是否是最后一列
//      * @param parent RecyclerView
//      * @param itemPosition ItemView的位置
//      * @param spanCount 一行有几列
//      * @param childCount ItemView的数量
//      * @return Boolean
//      */
//     protected open fun isLastColumn(
//         parent: RecyclerView, itemPosition: Int, spanCount: Int, childCount: Int
//     ): Boolean {
//         return when (val layoutManager = parent.layoutManager) {
//             is GridLayoutManager -> {
//                 val orientation = layoutManager.orientation
//                 if (orientation == StaggeredGridLayoutManager.VERTICAL) {
//                     isInRightEdge(spanCount, itemPosition)
//                 } else {
//                     isInBottomEdge(childCount, spanCount, itemPosition)
//                 }
//             }
//             is StaggeredGridLayoutManager -> {
//                 val orientation = layoutManager.orientation
//                 if (orientation == StaggeredGridLayoutManager.VERTICAL) {
//                     isInRightEdge(spanCount, itemPosition)
//                 } else {
//                     isInBottomEdge(childCount, spanCount, itemPosition)
//                 }
//             }
//             is LinearLayoutManager -> {
//                 isInRightEdge(spanCount, itemPosition)
//             }
//             else -> false
//         }
//     }
//
//     private fun isInRightEdge(spanCount: Int, itemPosition: Int): Boolean {
//         return (itemPosition + 1) % spanCount == 0
//     }
//
//     private fun isInBottomEdge(childCount: Int, spanCount: Int, itemPosition: Int): Boolean {
//         //一共多少行
//         val columns = childCount / spanCount + if (childCount % spanCount > 0) 1 else 0
//         //判断当前item是否是最后一行的item
//         return itemPosition >= (columns - 1) * spanCount
//     }
// }
