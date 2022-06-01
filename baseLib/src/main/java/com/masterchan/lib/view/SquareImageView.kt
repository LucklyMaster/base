package com.masterchan.lib.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * @author MasterChan
 * @date 2021-12-29 14:58
 * @describe SquareImageView
 */
class SquareImageView : AppCompatImageView {

    private var byWidth = true

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context, attrs, defStyleAttr
    )

    constructor(context: Context, byWidth: Boolean = true) : super(context) {
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