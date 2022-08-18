package com.master.lib.widget.span

import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import com.master.lib.ext.dp2px
import com.master.lib.ext.dp2pxi
import com.master.lib.ext.sp2px

/**
 * 设置文本的大小，与[android.text.style.RelativeSizeSpan]不同的是，需要一个精确的数值
 * @author: MasterChan
 * @date: 2022-8-18 17:15
 */
class ExactlySizePan(private val size: Float) : MetricAffectingSpan() {

    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.textSize = sp2px(size)
    }

    override fun updateMeasureState(textPaint: TextPaint) {
        textPaint.textSize = sp2px(size)
    }
}