package com.master.lib.ext

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.master.lib.widget.span.ExactlySizePan
import com.master.lib.widget.span.ExtendImageSpan
import com.master.lib.widget.span.TypefaceSpan

/**
 * 修改指定[range]文字的文字大小
 * @param range 文字范围
 * @param textSize 具体需要修改的值
 */
fun CharSequence.toSizeSpan(range: IntRange, textSize: Float): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            ExactlySizePan(textSize), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 修改指定[range]文字的文字前景色
 * @param range 文字范围
 * @param color 要改变的颜色
 */
fun CharSequence.toColorSpan(range: IntRange, color: Int): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            ForegroundColorSpan(color), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 修改指定[range]文字的文字背景色
 * @param range 文字范围
 * @param color 要改变的颜色
 */
fun CharSequence.toBackgroundColorSpan(range: IntRange, color: Int): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            BackgroundColorSpan(color), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 为指定[range]的文字添加删除线
 * @param range 文字范围
 */
fun CharSequence.toStrikethroughSpan(range: IntRange): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            StrikethroughSpan(), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 为指定[range]的文字添加点击事件
 * @param range 文字范围
 * @param textColor 文字颜色
 * @param clickListener 点击事件
 * @return CharSequence
 */
fun CharSequence.toClickSpan(
    range: IntRange,
    textColor: Int = 0,
    clickListener: ((View) -> Unit)?
): CharSequence {
    return SpannableString(this).apply {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickListener?.invoke(widget)
            }

            override fun updateDrawState(textPaint: TextPaint) {
                if (textColor != 0) {
                    textPaint.color = textColor
                }
            }
        }
        setSpan(clickableSpan, range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 为指定[range]的文字添加Style效果
 * @param range 文字范围
 * @param style 例如[Typeface.BOLD]
 */
fun CharSequence.toStyleSpan(range: IntRange, style: Int): CharSequence {
    return SpannableString(this).apply {
        setSpan(StyleSpan(style), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 为指定[range]的文字设置字体
 * @param range 文字范围
 * @param typeface 自定义字体
 */
fun CharSequence.toTypeFaceSpan(range: IntRange, typeface: Typeface): CharSequence {
    return SpannableString(this).apply {
        setSpan(TypefaceSpan(typeface), range.first, range.last, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}

/**
 * 为指定[range]的文字设置图片
 * @param range 文字范围
 * @param imageRes 图片
 * @param verticalAlignment 垂直对齐方式
 * @param leftMargin Int
 * @param rightMargin Int
 * @param imageWidth Int
 * @param imageHeight Int
 * @return CharSequence
 */
fun CharSequence.toImageSpan(
    range: IntRange,
    @DrawableRes imageRes: Int,
    verticalAlignment: Int = ExtendImageSpan.ALIGN_CENTER,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    imageWidth: Int = -1,
    imageHeight: Int = -1
): CharSequence {
    return toImageSpan(
        range, getDrawable(imageRes)!!, verticalAlignment, leftMargin, rightMargin, imageWidth,
        imageHeight
    )
}

/**
 * 为指定[range]的文字设置图片
 * @param range 文字范围
 * @param drawable 图片
 * @param verticalAlignment 垂直对齐方式
 * @param leftMargin Int
 * @param rightMargin Int
 * @param imageWidth Int
 * @param imageHeight Int
 * @return CharSequence
 */
fun CharSequence.toImageSpan(
    range: IntRange,
    drawable: Drawable,
    verticalAlignment: Int = ExtendImageSpan.ALIGN_CENTER,
    leftMargin: Int = 0,
    rightMargin: Int = 0,
    imageWidth: Int = -1,
    imageHeight: Int = -1
): CharSequence {
    return SpannableString(this).apply {
        setSpan(
            ExtendImageSpan(
                drawable.apply {
                    val width = if (imageWidth == -1) intrinsicWidth else imageWidth
                    val height = if (imageHeight == -1) intrinsicHeight else imageHeight
                    setBounds(0, 0, width, height)
                },
                verticalAlignment,
                leftMargin,
                rightMargin
            ),
            range.first,
            range.last,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

fun TextView.appendSizeSpan(text: String, textSize: Float) = apply {
    append(text.toSizeSpan(IntRange(0, text.length), textSize))
}

fun TextView.appendColorSpan(text: String, color: Int) = apply {
    append(text.toColorSpan(IntRange(0, text.length), color))
}

fun TextView.appendBackgroundColorSpan(text: String, color: Int) = apply {
    append(text.toBackgroundColorSpan(IntRange(0, text.length), color))
}

fun TextView.appendStrikethroughSpan(text: String) = apply {
    append(text.toStrikethroughSpan(IntRange(0, text.length)))
}

fun TextView.appendClickSpan(text: String, color: Int = 0, clickListener: (View) -> Unit) = apply {
    movementMethod = LinkMovementMethod.getInstance()
    //去掉点击后的背景颜色
    highlightColor = Color.TRANSPARENT
    append(text.toClickSpan(IntRange(0, text.length), color, clickListener))
}

fun TextView.appendStyleSpan(text: String, style: Int) = apply {
    append(text.toStyleSpan(style = style, range = IntRange(0, text.length)))
}

fun TextView.appendTypeFaceSpan(text: String, typeface: Typeface) = apply {
    append(text.toTypeFaceSpan(IntRange(0, text.length), typeface))
}

fun TextView.appendImageSpan(
    drawable: Drawable,
    verticalAlignment: Int = ExtendImageSpan.ALIGN_CENTER,
    imageWidth: Int = -1,
    imageHeight: Int = -1,
    leftMargin: Int = 0,
    rightMargin: Int = 0
) = apply {
    append(
        text.toImageSpan(
            IntRange(0, text.length), drawable, verticalAlignment, leftMargin, rightMargin,
            imageWidth, imageHeight
        )
    )
}

fun TextView.appendImageSpan(
    @DrawableRes res: Int,
    verticalAlignment: Int = ExtendImageSpan.ALIGN_CENTER,
    imageWidth: Int = -1,
    imageHeight: Int = -1,
    leftMargin: Int = 0,
    rightMargin: Int = 0
) = apply {
    appendImageSpan(
        getDrawable(res)!!, verticalAlignment, imageWidth, imageHeight, leftMargin, rightMargin
    )
}