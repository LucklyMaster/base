package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * 宽高相等的ImageView
 * @author MasterChan
 * @date 2021-12-29 14:58
 */
class SquareImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var byWidth = true

    constructor(context: Context, byWidth: Boolean = true) : this(context, null, 0) {
        this.byWidth = byWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (byWidth) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        }
    }
}