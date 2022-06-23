package com.mc.lib.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.color.MaterialColors
import com.masterchan.lib.R

/**
 * ShapeItemView
 * @author MasterChan
 * @date 2021-12-13 15:00
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_shapeItemViewDefaultStyle,
    defStyleRes: Int = R.style.mc_ShapeItemView
) : TitleBar(context, attrs, defStyleAttr, defStyleRes) {

    val helper: ShapeViewHelper = ShapeViewHelper()

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ShapeItemView, defStyleAttr, defStyleRes
        )
        val useRipple = a.getBoolean(R.styleable.ShapeItemView_mc_useRipple, true)
        val radius = a.getDimension(R.styleable.ShapeItemView_mc_radius, 0f)
        val ltRadius = a.getDimension(R.styleable.ShapeItemView_mc_leftTopRadius, radius)
        val lbRadius = a.getDimension(R.styleable.ShapeItemView_mc_leftBottomRadius, radius)
        val rtRadius = a.getDimension(R.styleable.ShapeItemView_mc_rightTopRadius, radius)
        val rbRadius = a.getDimension(R.styleable.ShapeItemView_mc_rightBottomRadius, radius)
        val strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeItemView_mc_strokeWidth, 0)

        val controlColor = MaterialColors.getColor(this, android.R.attr.colorControlHighlight)
        val rippleColor = a.getColor(R.styleable.ShapeItemView_mc_rippleColor, controlColor)
        val pressedColor = a.getColor(R.styleable.ShapeItemView_mc_pressedColor, controlColor)
        val strokeColor = a.getColor(R.styleable.ShapeItemView_mc_strokeColor, Color.GRAY)
        var normalColor = MaterialColors.getColor(this, android.R.attr.colorPrimary)
        normalColor = a.getColor(R.styleable.ShapeItemView_mc_normalColor, normalColor)
        val disableColor = a.getColor(
            R.styleable.ShapeItemView_mc_disableColor, Color.parseColor("#BFBFBF")
        )

        val circle = a.getBoolean(R.styleable.ShapeItemView_mc_circle, false)

        if (a.hasValue(R.styleable.ShapeItemView_mc_leftEditable)) {
            leftItem.labelView.editable = a.getBoolean(
                R.styleable.ShapeItemView_mc_leftEditable, false
            )
        }
        if (a.hasValue(R.styleable.ShapeItemView_mc_middleEditable)) {
            middleItem.labelView.editable = a.getBoolean(
                R.styleable.ShapeItemView_mc_middleEditable, false
            )
        }
        if (a.hasValue(R.styleable.ShapeItemView_mc_rightEditable)) {
            rightItem.labelView.editable = a.getBoolean(
                R.styleable.ShapeItemView_mc_rightEditable, false
            )
        }

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