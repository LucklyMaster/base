package com.master.lib.widget.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.style.ImageSpan
import androidx.annotation.DrawableRes
import com.master.lib.ext.getDrawable

/**
 * 对[ImageSpan]进行扩展，支持垂直对齐和左右间距
 * @author: MasterChan
 * @date: 2022-8-18 16:18
 */
open class ExtendImageSpan @JvmOverloads constructor(
    drawable: Drawable,
    verticalAlignment: Int = 2,
    private val leftMargin: Int = 0,
    private val rightMargin: Int = 0
) : ImageSpan(drawable, verticalAlignment) {

    companion object Align {
        const val ALIGN_CENTER = 3
    }

    constructor(@DrawableRes res: Int, verticalAlignment: Int) : this(
        getDrawable(res)!!, verticalAlignment
    )

    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int,
        fontMetricsInt: FontMetricsInt?
    ): Int {
        val drawable = drawable
        val rect = drawable.bounds
        if (null != fontMetricsInt) {
            val fmPaint = paint.fontMetricsInt
            val fontHeight = fmPaint.descent - fmPaint.ascent
            val drHeight = rect.bottom - rect.top
            val centerY = fmPaint.ascent + fontHeight / 2
            fontMetricsInt.ascent = centerY - drHeight / 2
            fontMetricsInt.top = fontMetricsInt.ascent
            fontMetricsInt.bottom = centerY + drHeight / 2
            fontMetricsInt.descent = fontMetricsInt.bottom
        }
        return leftMargin + rect.right + rightMargin
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val transX = x + leftMargin
        if (verticalAlignment == ALIGN_CENTER) {
            val drawable = drawable
            canvas.save()
            val fmPaint = paint.fontMetricsInt
            val fontHeight = fmPaint.descent - fmPaint.ascent
            val centerY = y + fmPaint.descent - fontHeight / 2
            val transY = centerY - (drawable.bounds.bottom - drawable.bounds.top) / 2
            canvas.translate(transX, transY.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        } else {
            super.draw(canvas, text, start, end, transX, top, y, bottom, paint)
        }
    }
}
