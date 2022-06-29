package com.master.lib.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.Drawable
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
class ShapeViewHelper() {

    var useRipple = false
    var strokeWidth = 0
    var stokeColor = 0

    /**
     * 正常时的颜色
     */
    var normalColor = 0

    /**
     * [View.isEnabled]为false时的颜色
     */
    var disableColor = 0

    /**
     * 按下后的颜色，如果[useRipple]为true，不生效，使用[rippleColor]
     */
    var pressedColor = 0

    /**
     * 水波纹颜色
     */
    var rippleColor = 0
    var leftTopRadius = 0f
    var leftBottomRadius = 0f
    var rightTopRadius = 0f
    var rightBottomRadius = 0f
    var isCircle = false

    fun setUseRipple(useRipple: Boolean) = apply {
        this.useRipple = useRipple
    }

    fun setStrokeWidth(stokeWidth: Int) = apply {
        this.strokeWidth = stokeWidth
    }

    fun setStrokeColor(@ColorInt stokeColor: Int) = apply {
        this.stokeColor = stokeColor
    }

    fun setNormalColor(@ColorInt normalColor: Int) = apply {
        this.normalColor = normalColor
    }

    fun setPressedColor(@ColorInt pressedColor: Int) = apply {
        this.pressedColor = pressedColor
    }

    fun setDisableColor(@ColorInt disableColor: Int) = apply {
        this.disableColor = disableColor
    }

    fun setRippleColor(@ColorInt rippleColor: Int) = apply {
        this.rippleColor = rippleColor
    }

    fun setLeftTopRadius(radius: Float) = apply {
        leftTopRadius = radius
    }

    fun setLeftBottomRadius(radius: Float) = apply {
        leftBottomRadius = radius
    }

    fun setRightTopRadius(radius: Float) = apply {
        rightTopRadius = radius
    }

    fun setRightBottomRadius(radius: Float) = apply {
        rightBottomRadius = radius
    }

    fun setRadius(radius: Float) = apply {
        leftTopRadius = radius
        leftBottomRadius = radius
        rightTopRadius = radius
        rightBottomRadius = radius
    }

    fun setCircle(isCircle: Boolean) = apply {
        this.isCircle = isCircle
    }

    fun into(view: View) {
        if (isCircle) {
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
        val drawable = if (useRipple) {
            getShapeRippleDrawable()
        } else {
            getShapeDrawable()
        }
        view.background = drawable
    }

    private fun getShapeRippleDrawable(): Drawable {
        //此处的state添加顺序，经测试，改变后会导致state切换失效
        val drawable = StateListDrawable()
        val radii = floatArrayOf(
            leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius,
            rightBottomRadius, leftBottomRadius, leftBottomRadius
        )
        //正常
        val content = GradientDrawable().apply {
            cornerRadii = radii
            setColor(normalColor)
            setStroke(strokeWidth, stokeColor)
        }
        val normalDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor), content, null
        )
        drawable.addState(intArrayOf(android.R.attr.state_enabled), normalDrawable)

        //禁用
        val disableDrawable = GradientDrawable().apply {
            setStroke(strokeWidth, stokeColor)
            setColor(disableColor)
            cornerRadii = radii.copyOf()
        }
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), disableDrawable)

        //按下
        drawable.addState(intArrayOf(-android.R.attr.state_pressed), normalDrawable)

        return drawable
    }

    private fun getShapeDrawable(): Drawable {
        val drawable = StateListDrawable()
        val radii = floatArrayOf(
            leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius,
            rightBottomRadius, leftBottomRadius, leftBottomRadius
        )

        //正常
        val normalDrawable = GradientDrawable().apply {
            cornerRadii = radii
            setStroke(strokeWidth, stokeColor)
            setColor(normalColor)
        }
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed),
            normalDrawable
        )

        //按下
        val pressedDrawable = GradientDrawable().apply {
            cornerRadii = radii
            setStroke(strokeWidth, stokeColor)
            setColor(pressedColor)
        }
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
            pressedDrawable
        )

        //禁用
        val disableDrawable = GradientDrawable().apply {
            cornerRadii = radii
            setStroke(strokeWidth, stokeColor)
            setColor(Color.GRAY)
        }
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), disableDrawable)

        return drawable
    }
}