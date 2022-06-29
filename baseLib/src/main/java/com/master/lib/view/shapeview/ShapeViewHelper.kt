package com.master.lib.view.shapeview

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
import com.masterchan.lib.R
import kotlin.math.min

/**
 * ShapeViewHelper
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeViewHelper(attrs: AttributeSet? = null) : IShapeView {

    var view: View? = null
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
    var isCircle = false

    init {
        // if (view != null && attrs != null) {
        initWithAttrs(view!!, attrs)
        // }
    }

    private fun initWithAttrs(view: View, attrs: AttributeSet?) {
        val context = view.context
        val controlColor = context.getColor(R.color.color_ripple)
        var normalColor = MaterialColors.getColor(context, android.R.attr.colorPrimary, 0)

        val a = context.obtainStyledAttributes(attrs, R.styleable.ShapeView)
        val useRipple = a.getBoolean(R.styleable.ShapeView_mc_useRipple, true)
        val radius = a.getDimension(R.styleable.ShapeView_mc_radius, 0f)
        val ltRadius = a.getDimension(R.styleable.ShapeView_mc_leftTopRadius, radius)
        val lbRadius = a.getDimension(R.styleable.ShapeView_mc_leftBottomRadius, radius)
        val rtRadius = a.getDimension(R.styleable.ShapeView_mc_rightTopRadius, radius)
        val rbRadius = a.getDimension(R.styleable.ShapeView_mc_rightBottomRadius, radius)
        val strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeView_mc_strokeWidth, 0)

        val rippleColor = a.getColor(R.styleable.ShapeView_mc_rippleColor, controlColor)
        val pressedColor = a.getColor(R.styleable.ShapeView_mc_pressedColor, controlColor)
        val strokeColor = a.getColor(R.styleable.ShapeView_mc_strokeColor, Color.GRAY)

        normalColor = a.getColor(R.styleable.ShapeView_mc_normalColor, normalColor)
        val disableColor = a.getColor(
            R.styleable.ShapeView_mc_disableColor, context.getColor(R.color.color_disable)
        )

        val circle = a.getBoolean(R.styleable.ShapeView_mc_circle, false)
        a.recycle()

        this.setStrokeWidth(strokeWidth)
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
            .into(view)
    }

    override fun bindView(view: View) = apply {
        this.view = view
    }

    override fun setUseRipple(useRipple: Boolean) = apply {
        this.useRipple = useRipple
    }

    override fun setStrokeWidth(stokeWidth: Int) = apply {
        this.strokeWidth = stokeWidth
    }

    override fun setStrokeColor(@ColorInt stokeColor: Int) = apply {
        this.stokeColor = stokeColor
    }

    override fun setNormalColor(@ColorInt normalColor: Int) = apply {
        this.normalColor = normalColor
    }

    /**
     * 按下后的颜色，如果[useRipple]为true，不生效，使用[rippleColor]
     * @param pressedColor Int
     * @return ShapeViewHelper
     */
    override fun setPressedColor(@ColorInt pressedColor: Int) = apply {
        this.pressedColor = pressedColor
    }

    override fun setDisableColor(@ColorInt disableColor: Int) = apply {
        this.disableColor = disableColor
    }

    override fun setRippleColor(@ColorInt rippleColor: Int) = apply {
        this.rippleColor = rippleColor
    }

    override fun setLeftTopRadius(radius: Float) = apply {
        leftTopRadius = radius
    }

    override fun setLeftBottomRadius(radius: Float) = apply {
        leftBottomRadius = radius
    }

    override fun setRightTopRadius(radius: Float) = apply {
        rightTopRadius = radius
    }

    override fun setRightBottomRadius(radius: Float) = apply {
        rightBottomRadius = radius
    }

    override fun setRadius(radius: Float) = apply {
        leftTopRadius = radius
        leftBottomRadius = radius
        rightTopRadius = radius
        rightBottomRadius = radius
    }

    override fun setCircle(isCircle: Boolean) = apply {
        this.isCircle = isCircle
    }

    override fun into() {
        if (view == null) {
            throw IllegalArgumentException("the bind view is null")
        }
        into(view!!)
    }

    override fun into(view: View) {
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