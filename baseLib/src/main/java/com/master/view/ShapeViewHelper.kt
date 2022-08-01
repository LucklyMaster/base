package com.master.view

import android.content.res.ColorStateList
import android.content.res.TypedArray
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
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors
import com.master.R
import com.master.ext.equals
import com.master.ext.getColor
import com.master.ext.isNotEmpty
import kotlin.math.min

/**
 * ShapeView实现的帮助类，可以使用属性[R.attr.mc_shapeViewDefaultStyle]进行全局配置
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeViewHelper(val view: View, attrs: AttributeSet? = null) {

    var isCircle: Boolean
        private set
    var useRipple: Boolean
        private set
    var rippleColor: Int
        private set
    var normalColor: Int
        private set
    var effectColor: Int
        private set
    var disableColor: Int
        private set
    var strokeWidth: Int
        private set
    var strokeColor: Int
        private set
    var dashWidth: Float
        private set
    var dashGap: Float
        private set
    var shapeWidth: Int
        private set
    var shapeHeight: Int
        private set
    var leftTopRadius: Float
        private set
    var leftBottomRadius: Float
        private set
    var rightTopRadius: Float
        private set
    var rightBottomRadius: Float
        private set

    //以下属性只在useRipple为false时生效
    var effectMode: Int
        private set
    var normalDrawable: Drawable?
        private set
    var effectDrawable: Drawable?
        private set
    var disableDrawable: Drawable?
        private set
    var gradientType: Int
        private set
    var gradientAngle: Int
        private set
    var gradientRadius: Float
        private set
    var gradientCenterX: Float
        private set
    var gradientCenterY: Float
        private set
    var startColor: Int
        private set
    var centerColor: Int
        private set
    var endColor: Int
        private set

    companion object {
        const val EFFECT_PRESSED = 0
        const val EFFECT_SELECTED = 1
    }

    init {
        val context = view.context
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ShapeView, R.attr.mc_shapeViewDefaultStyle, 0
        )

        isCircle = a.getBoolean(R.styleable.ShapeView_mc_isCircle, false)
        useRipple = a.getBoolean(R.styleable.ShapeView_mc_useRipple, true)
        rippleColor = a.getColorRes(R.styleable.ShapeView_mc_rippleColor, R.color.color_ripple)
        normalColor = if (view.isInEditMode) {
            Color.parseColor("#FF6200EE")
        } else {
            MaterialColors.getColor(context, android.R.attr.colorPrimary, 0)
        }
        normalColor = a.getColor(R.styleable.ShapeView_mc_normalColor, normalColor)
        effectColor = a.getColor(R.styleable.ShapeView_mc_effectColor, "#FF3700B3")
        disableColor = a.getColor(R.styleable.ShapeView_mc_disableColor, "#BFBFBF")
        normalDrawable = a.getDrawable(R.styleable.ShapeView_mc_normalDrawable)
        effectDrawable = a.getDrawable(R.styleable.ShapeView_mc_effectDrawable)
        disableDrawable = a.getDrawable(R.styleable.ShapeView_mc_disableDrawable)

        strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeView_mc_strokeWidth, 0)
        strokeColor = a.getColor(R.styleable.ShapeView_mc_strokeColor, "#D6D6D6")
        dashWidth = a.getDimension(R.styleable.ShapeView_mc_dashWidth, 0f)
        dashGap = a.getDimension(R.styleable.ShapeView_mc_dashGap, 0f)
        shapeWidth = a.getDimensionPixelOffset(R.styleable.ShapeView_mc_shapeWidth, -1)
        shapeHeight = a.getDimensionPixelOffset(R.styleable.ShapeView_mc_shapeHeight, -1)

        val radius = a.getDimension(R.styleable.ShapeView_mc_radius, 0f)
        leftTopRadius = a.getDimension(R.styleable.ShapeView_mc_leftTopRadius, radius)
        leftBottomRadius = a.getDimension(R.styleable.ShapeView_mc_leftBottomRadius, radius)
        rightTopRadius = a.getDimension(R.styleable.ShapeView_mc_rightTopRadius, radius)
        rightBottomRadius = a.getDimension(R.styleable.ShapeView_mc_rightBottomRadius, radius)

        effectMode = a.getInteger(R.styleable.ShapeView_mc_effectMode, EFFECT_PRESSED)
        //默认GradientDrawable.LINEAR_GRADIENT
        gradientType = a.getInteger(R.styleable.ShapeView_mc_gradientType, 0)
        gradientAngle = a.getInteger(R.styleable.ShapeView_mc_gradientAngle, 0)
        gradientRadius = a.getDimension(R.styleable.ShapeView_mc_gradientRadius, 0f)
        gradientCenterX = a.getFloat(R.styleable.ShapeView_mc_gradientCenterX, 0.5f)
        gradientCenterY = a.getFloat(R.styleable.ShapeView_mc_gradientCenterY, 0.5f)
        startColor = a.getColor(R.styleable.ShapeView_mc_gradientStartColor, 0)
        centerColor = a.getColor(R.styleable.ShapeView_mc_gradientCenterColor, 0)
        endColor = a.getColor(R.styleable.ShapeView_mc_gradientEndColor, 0)

        a.recycle()
    }

    private fun TypedArray.getColorRes(@StyleableRes index: Int, @ColorRes defValue: Int): Int {
        return getColor(index, ContextCompat.getColor(view.context, defValue))
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
        this.effectColor = pressedColor
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
        val normalDrawable = normalDrawable ?: GradientDrawable().apply {
            setGradientDrawable(this, normalColor, radii)
        }
        val state = if (effectMode == EFFECT_PRESSED) {
            android.R.attr.state_pressed
        } else {
            android.R.attr.state_selected
        }
        drawable.addState(intArrayOf(android.R.attr.state_enabled, -state), normalDrawable)

        //按下
        val pressedDrawable = effectDrawable ?: GradientDrawable().apply {
            setGradientDrawable(this, effectColor, radii)
        }
        drawable.addState(intArrayOf(android.R.attr.state_enabled, state), pressedDrawable)

        //禁用
        val disableDrawable = disableDrawable ?: GradientDrawable().apply {
            setGradientDrawable(this, disableColor, radii)
        }
        drawable.addState(intArrayOf(-android.R.attr.state_enabled), disableDrawable)

        return drawable
    }

    private fun setGradientDrawable(drawable: GradientDrawable, color: Int, radii: FloatArray) {
        if (isCircle) drawable.shape = GradientDrawable.OVAL else GradientDrawable.RECTANGLE
        drawable.cornerRadii = radii
        drawable.setStroke(strokeWidth, strokeColor, dashWidth, dashGap)
        drawable.setColor(color)

        if (!useRipple) {
            drawable.gradientType = this@ShapeViewHelper.gradientType
            setGradientDrawableAngle(drawable, gradientAngle)
            drawable.gradientRadius = this@ShapeViewHelper.gradientRadius
            drawable.setGradientCenter(gradientCenterX, gradientCenterY)

            val colors = mutableListOf<Int>()
            if (startColor != 0) colors.add(startColor)
            if (centerColor != 0) colors.add(centerColor)
            if (endColor != 0) colors.add(endColor)
            colors.isNotEmpty { drawable.colors = this.toIntArray() }
        }
    }

    private fun setGradientDrawableAngle(drawable: GradientDrawable, angle: Int) {
        when (angle) {
            0 -> drawable.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            45 -> drawable.orientation = GradientDrawable.Orientation.BL_TR
            90 -> drawable.orientation = GradientDrawable.Orientation.BOTTOM_TOP
            135 -> drawable.orientation = GradientDrawable.Orientation.BR_TL
            180 -> drawable.orientation = GradientDrawable.Orientation.RIGHT_LEFT
            225 -> drawable.orientation = GradientDrawable.Orientation.TR_BL
            270 -> drawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM
            315 -> drawable.orientation = GradientDrawable.Orientation.TL_BR
            else -> drawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM
        }
    }
}