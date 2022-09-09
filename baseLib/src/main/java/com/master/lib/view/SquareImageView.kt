package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * 宽高相等的ImageView
 * @author MasterChan
 * @date 2021-12-29 14:58
 */
open class SquareImageView @JvmOverloads constructor(
    context: Context,
    private var baseWith: Boolean = true,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    protected open var isForceSquare = true

    fun setForceSquare(isForceSquare: Boolean) = apply {
        this.isForceSquare = isForceSquare
        requestLayout()
    }

    fun setBaseWidth(baseWith: Boolean) = apply {
        this.baseWith = baseWith
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec
        if (isForceSquare) {
            if (baseWith) {
                widthSpec = widthMeasureSpec
                heightSpec = widthMeasureSpec
            } else {
                widthSpec = heightMeasureSpec
                heightSpec = heightMeasureSpec
            }
        }
        super.onMeasure(widthSpec, heightSpec)
    }
}