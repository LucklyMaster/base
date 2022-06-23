package com.mc.lib.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import kotlin.math.min

/**
 * ShapeViewHelper
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeViewHelper {

    var useRipple = false
    var strokeWidth = 0
    var stokeColor = 0
    var normalColor = 0
    var disableColor = 0
    var pressedColor = 0
    var rippleColor = 0
    var leftTopRadius = 0f
    var leftBottomRadius = 0f
    var rightTopRadius = 0f
    var rightBottomRadius = 0f
    var circle = false

    fun setUseRipple(useRipple: Boolean): ShapeViewHelper {
        this.useRipple = useRipple
        return this
    }

    fun setStrokeWidth(stokeWidth: Int): ShapeViewHelper {
        this.strokeWidth = stokeWidth
        return this
    }

    fun setStrokeColor(@ColorInt stokeColor: Int): ShapeViewHelper {
        this.stokeColor = stokeColor
        return this
    }

    fun setNormalColor(@ColorInt normalColor: Int): ShapeViewHelper {
        this.normalColor = normalColor
        return this
    }

    fun setPressedColor(@ColorInt pressedColor: Int): ShapeViewHelper {
        this.pressedColor = pressedColor
        return this
    }

    fun setDisableColor(@ColorInt disableColor: Int): ShapeViewHelper {
        this.disableColor = disableColor
        return this
    }

    fun setRippleColor(@ColorInt rippleColor: Int): ShapeViewHelper {
        this.rippleColor = rippleColor
        return this
    }

    fun setLeftTopRadius(radius: Float): ShapeViewHelper {
        leftTopRadius = radius
        return this
    }

    fun setLeftBottomRadius(radius: Float): ShapeViewHelper {
        leftBottomRadius = radius
        return this
    }

    fun setRightTopRadius(radius: Float): ShapeViewHelper {
        rightTopRadius = radius
        return this
    }

    fun setRightBottomRadius(radius: Float): ShapeViewHelper {
        rightBottomRadius = radius
        return this
    }

    fun setRadius(radius: Float): ShapeViewHelper {
        leftTopRadius = radius
        leftBottomRadius = radius
        rightTopRadius = radius
        rightBottomRadius = radius
        return this
    }

    fun setCircle(circle: Boolean): ShapeViewHelper {
        this.circle = circle
        return this
    }

    fun into(view: View) {
        if (circle) {
            view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val size = min(view.width, view.height)
                    val rect = Rect(0, 0, size, size)
                    outline.setRoundRect(rect, (size shr 1).toFloat())
                }
            }
            view.clipToOutline = true
        }
        setDrawable(view)
    }

    private fun setDrawable(view: View) {
        if (useRipple) {
            setShapeRippleDrawable(view)
        } else {
            setShapeDrawable(view)
        }
    }

    private fun setShapeRippleDrawable(view: View) {
        val drawable = StateListDrawable()

        val normalDrawable = GradientDrawable()
        var radii = floatArrayOf(
            leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius,
            rightBottomRadius, leftBottomRadius, leftBottomRadius
        )
        normalDrawable.cornerRadii = radii
        normalDrawable.setColor(normalColor)
        normalDrawable.setStroke(strokeWidth, stokeColor)
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed), normalDrawable
        )

        val gradientDrawable = GradientDrawable()
        radii = floatArrayOf(
            leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius,
            rightBottomRadius, leftBottomRadius, leftBottomRadius
        )
        gradientDrawable.cornerRadii = radii
        gradientDrawable.setColor(normalColor)
        gradientDrawable.setStroke(strokeWidth, stokeColor)
        val pressedDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor), gradientDrawable, null
        )
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
            pressedDrawable
        )

        val disableDrawable = GradientDrawable()
        disableDrawable.setStroke(strokeWidth, stokeColor)
        disableDrawable.setColor(disableColor)
        disableDrawable.cornerRadii = radii
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), disableDrawable)

        view.background = drawable
    }

    private fun setShapeDrawable(view: View) {
        val drawable = StateListDrawable()
        val normalDrawable = GradientDrawable()
        normalDrawable.setStroke(strokeWidth, stokeColor)
        normalDrawable.setColor(normalColor)
        val radii = floatArrayOf(
            leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius,
            rightBottomRadius, leftBottomRadius, leftBottomRadius
        )
        normalDrawable.cornerRadii = radii
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed),
            normalDrawable
        )
        val pressedDrawable = GradientDrawable()
        pressedDrawable.setStroke(strokeWidth, stokeColor)
        pressedDrawable.setColor(pressedColor)
        pressedDrawable.cornerRadii = radii
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
            pressedDrawable
        )
        val disableDrawable = GradientDrawable()
        disableDrawable.setStroke(strokeWidth, stokeColor)
        disableDrawable.setColor(Color.GRAY)
        disableDrawable.cornerRadii = radii
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), disableDrawable)

        view.background = drawable
    }
}