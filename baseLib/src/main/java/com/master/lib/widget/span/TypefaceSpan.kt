package com.master.lib.widget.span

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * 与[android.text.style.StyleSpan]功能相同，不同的是支持自定义字体
 * @author: MasterChan
 * @date: 2022-8-18 18:46
 */
open class TypefaceSpan(private val typeface: Typeface) : MetricAffectingSpan() {

    override fun updateDrawState(drawState: TextPaint) {
        apply(drawState)
    }

    override fun updateMeasureState(paint: TextPaint) {
        apply(paint)
    }

    private fun apply(paint: Paint) {
        val oldTypeface = paint.typeface
        val oldStyle = oldTypeface?.style ?: 0
        val fakeStyle = oldStyle and typeface.style.inv()
        if (fakeStyle and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fakeStyle and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }
        paint.typeface = typeface
    }
}