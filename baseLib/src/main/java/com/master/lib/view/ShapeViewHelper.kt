package com.master.lib.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import com.google.android.material.color.MaterialColors
import com.master.lib.ext.equals
import com.master.lib.ext.getColor
import com.masterchan.lib.R
import kotlin.math.min

/**
 * ShapeView实现的帮助类，可以使用属性[R.attr.mc_shapeViewDefaultStyle]进行全局配置
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeViewHelper(val view: View, attrs: AttributeSet? = null) {

    var isCircle = false
        private set
    var useRipple = false
        private set
    var strokeWidth = 0
        private set
    var strokeColor = 0
        private set
    var normalColor = 0
        private set
    var pressedColor = 0
        private set
    var disableColor = 0
        private set
    var rippleColor = 0
        private set
    var leftTopRadius = 0f
        private set
    var leftBottomRadius = 0f
        private set
    var rightTopRadius = 0f
        private set
    var rightBottomRadius = 0f
        private set

    init {
        val context = view.context
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ShapeView, R.attr.mc_shapeViewDefaultStyle, 0
        )

        isCircle = a.getBoolean(R.styleable.ShapeView_mc_circle, false)
        useRipple = a.getBoolean(R.styleable.ShapeView_mc_useRipple, true)

        rippleColor = a.getColor(
            R.styleable.ShapeView_mc_rippleColor, context.getColor(R.color.color_ripple)
        )
        normalColor = if (view.isInEditMode) {
            Color.parseColor("#FF6200EE")
        } else {
            MaterialColors.getColor(context, android.R.attr.colorPrimary, 0)
        }
        normalColor = a.getColor(R.styleable.ShapeView_mc_normalColor, normalColor)
        pressedColor = a.getColor(R.styleable.ShapeView_mc_pressedColor, "#FF3700B3")
        strokeColor = a.getColor(R.styleable.ShapeView_mc_strokeColor, "#D6D6D6")
        disableColor = a.getColor(R.styleable.ShapeView_mc_disableColor, "#BFBFBF")

        strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeView_mc_strokeWidth, 0)
        val radius = a.getDimension(R.styleable.ShapeView_mc_radius, 0f)
        leftTopRadius = a.getDimension(R.styleable.ShapeView_mc_leftTopRadius, radius)
        leftBottomRadius = a.getDimension(R.styleable.ShapeView_mc_leftBottomRadius, radius)
        rightTopRadius = a.getDimension(R.styleable.ShapeView_mc_rightTopRadius, radius)
        rightBottomRadius = a.getDimension(R.styleable.ShapeView_mc_rightBottomRadius, radius)

        a.recycle()

        // TODO: 资源文件按state切换
    }

    fun setUseRipple(useRipple: Boolean) = apply {
        this.useRipple = useRipple
    }

    fun setStrokeWidth(stokeWidth: Int) = apply {
        this.strokeWidth = stokeWidth
    }

    fun setStrokeColor(@ColorInt stokeColor: Int) = apply {
        this.strokeColor = stokeColor
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

    fun into() {
        rightTopRadius.inc()
        val radiusEquals = leftTopRadius.equals(leftBottomRadius, rightTopRadius, rightBottomRadius)
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                when {
                    isCircle -> {
                        val size = min(view.width, view.height)
                        setRadius((size shr 1).toFloat())
                        val left = (view.width - size) / 2
                        val top = (view.height - size) / 2
                        val right = left + size
                        val bottom = top + size
                        val rect = Rect(left, top, right, bottom)
                        outline.setRoundRect(rect, (size shr 1).toFloat())
                    }
                    radiusEquals -> {
                        outline.setRoundRect(0, 0, view.width, view.height, leftTopRadius)
                    }
                    else -> {
                        outline.setRoundRect(0, 0, view.width, view.height, 0f)
                    }
                }
            }
        }
        view.clipToOutline = true
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
            if (isCircle) shape = GradientDrawable.OVAL else GradientDrawable.RECTANGLE
            cornerRadii = radii
            setColor(normalColor)
            setStroke(strokeWidth, strokeColor)
        }
        val normalDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor), content, null
        )
        drawable.addState(intArrayOf(android.R.attr.state_enabled), normalDrawable)

        //禁用
        val disableDrawable = GradientDrawable().apply {
            if (isCircle) shape = GradientDrawable.OVAL else GradientDrawable.RECTANGLE
            setStroke(strokeWidth, strokeColor)
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
            if (isCircle) shape = GradientDrawable.OVAL else GradientDrawable.RECTANGLE
            cornerRadii = radii
            setStroke(strokeWidth, strokeColor)
            setColor(normalColor)
        }
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed),
            normalDrawable
        )

        //按下
        val pressedDrawable = GradientDrawable().apply {
            if (isCircle) shape = GradientDrawable.OVAL else GradientDrawable.RECTANGLE
            cornerRadii = radii
            setStroke(strokeWidth, strokeColor)
            setColor(pressedColor)
        }
        drawable.addState(
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
            pressedDrawable
        )

        //禁用
        val disableDrawable = GradientDrawable().apply {
            if (isCircle) shape = GradientDrawable.OVAL else GradientDrawable.RECTANGLE
            cornerRadii = radii
            setStroke(strokeWidth, strokeColor)
            setColor(Color.GRAY)
        }
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), disableDrawable)

        return drawable
    }
}