package com.master.lib.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.color.MaterialColors
import com.masterchan.lib.R

/**
 * ShapeTextView
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    val helper: ShapeViewHelper = ShapeViewHelper()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShapeTextView)
        val useRipple = a.getBoolean(R.styleable.ShapeTextView_mc_useRipple, true)
        val radius = a.getDimension(R.styleable.ShapeTextView_mc_radius, 0f)
        val ltRadius = a.getDimension(R.styleable.ShapeTextView_mc_leftTopRadius, radius)
        val lbRadius = a.getDimension(R.styleable.ShapeTextView_mc_leftBottomRadius, radius)
        val rtRadius = a.getDimension(R.styleable.ShapeTextView_mc_rightTopRadius, radius)
        val rbRadius = a.getDimension(R.styleable.ShapeTextView_mc_rightBottomRadius, radius)
        val strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeTextView_mc_strokeWidth, 0)

        val controlColor = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorControlHighlight)
        val rippleColor = a.getColor(R.styleable.ShapeTextView_mc_rippleColor, controlColor)
        val pressedColor = a.getColor(R.styleable.ShapeTextView_mc_pressedColor, controlColor)
        val strokeColor = a.getColor(R.styleable.ShapeTextView_mc_strokeColor, Color.GRAY)
        var normalColor = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary)
        normalColor = a.getColor(R.styleable.ShapeTextView_mc_normalColor, normalColor)
        val disableColor = a.getColor(
            R.styleable.ShapeTextView_mc_disableColor, Color.parseColor("#BFBFBF")
        )

        val circle = a.getBoolean(R.styleable.ShapeTextView_mc_circle, false)
        a.recycle()

        helper.setStrokeWidth(strokeWidth)
            .setStrokeColor(strokeColor)
            .setRippleColor(rippleColor)
            .setNormalColor(normalColor)
            .setPressedColor(pressedColor)
            .setDisableColor(disableColor)
            .setLeftTopRadius(ltRadius)
            .setLeftBottomRadius(lbRadius)
            .setRightTopRadius(rtRadius)
            .setRightBottomRadius(rbRadius)
            .setUseRipple(useRipple)
            .setCircle(circle)
            .into(this)
    }
}