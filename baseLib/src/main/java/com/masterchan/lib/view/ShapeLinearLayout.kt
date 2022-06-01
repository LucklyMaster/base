package com.masterchan.lib.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import com.google.android.material.color.MaterialColors
import com.masterchan.lib.R

/**
 * @author MasterChan
 * @date 2021-12-10 10:18
 * @describe ShapeLinearLayout
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val helper: ShapeViewHelper = ShapeViewHelper()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShapeLinearLayout)
        val useRipple = a.getBoolean(R.styleable.ShapeLinearLayout_mc_useRipple, true)
        val radius = a.getDimension(R.styleable.ShapeLinearLayout_mc_radius, 0f)
        val ltRadius = a.getDimension(R.styleable.ShapeLinearLayout_mc_leftTopRadius, radius)
        val lbRadius = a.getDimension(R.styleable.ShapeLinearLayout_mc_leftBottomRadius, radius)
        val rtRadius = a.getDimension(R.styleable.ShapeLinearLayout_mc_rightTopRadius, radius)
        val rbRadius = a.getDimension(R.styleable.ShapeLinearLayout_mc_rightBottomRadius, radius)
        val strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeLinearLayout_mc_strokeWidth, 0)

        val controlColor = MaterialColors.getColor(this, android.R.attr.colorControlHighlight)
        val rippleColor = a.getColor(R.styleable.ShapeLinearLayout_mc_rippleColor, controlColor)
        val pressedColor = a.getColor(R.styleable.ShapeLinearLayout_mc_pressedColor, controlColor)
        val strokeColor = a.getColor(R.styleable.ShapeLinearLayout_mc_strokeColor, Color.GRAY)
        var normalColor = MaterialColors.getColor(this, android.R.attr.colorPrimary)
        normalColor = a.getColor(R.styleable.ShapeLinearLayout_mc_normalColor, normalColor)
        val disableColor = a.getColor(
            R.styleable.ShapeLinearLayout_mc_disableColor, Color.parseColor("#BFBFBF")
        )

        val circle = a.getBoolean(R.styleable.ShapeLinearLayout_mc_circle, false)
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