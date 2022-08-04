package com.master.lib.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.master.lib.R
import com.master.lib.ext.gone
import com.master.lib.ext.ifHas
import com.master.lib.ext.visible

/**
 * CellView
 * @author MasterChan
 * @date 2021-12-07 14:16
 */
@Suppress("MemberVisibilityCanBePrivate", "LeakingThis")
open class CellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    val iconView = ImageView(context)
    open val labelView = TextView(context)

    var iconGravity = Gravity.TOP
        private set

    val icon: Drawable?
        get() {
            return iconView.drawable
        }

    val text: CharSequence
        get() {
            return labelView.text.toString()
        }

    init {
        initView()

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.CellView, defStyleAttr, defStyleRes
        )
        if (!a.hasValue(R.styleable.CellView_android_gravity)) {
            gravity = Gravity.CENTER
        }
        a.ifHas(R.styleable.CellView_mc_icon) { setIcon(a.getDrawable(it)) }
        a.ifHas(R.styleable.CellView_mc_icon) { setIcon(a.getDrawable(it)) }
        a.ifHas(R.styleable.CellView_mc_iconColor) { setIconColor(a.getColorStateList(it)) }
        a.ifHas(R.styleable.CellView_mc_iconWidth) {
            setIconWidth(a.getLayoutDimension(it, 0))
        }
        a.ifHas(R.styleable.CellView_mc_iconHeight) {
            setIconHeight(a.getLayoutDimension(it, 0))
        }
        setText(a.getString(R.styleable.CellView_mc_text))
        a.ifHas(R.styleable.CellView_mc_textWidth) {
            setTextWidth(a.getLayoutDimension(it, 0))
        }
        a.ifHas(R.styleable.CellView_mc_textHeight) {
            setTextHeight(a.getLayoutDimension(it, 0))
        }
        setTextPadding(a)
        labelView.gravity = a.getInteger(R.styleable.CellView_mc_textGravity, Gravity.CENTER)
        a.ifHas(R.styleable.CellView_mc_textColor) { setTextColor(a.getColorStateList(it)) }
        a.ifHas(R.styleable.CellView_mc_textSize) { setTextSize(a.getDimension(it, 0f)) }
        a.ifHas(R.styleable.CellView_mc_textMaxWidth) {
            setTextMaxWidth(a.getDimensionPixelOffset(it, 0))
        }
        a.ifHas(R.styleable.CellView_mc_iconGravity) { setIconGravity(a.getInt(it, 0)) }
        a.ifHas(R.styleable.CellView_mc_iconPadding) {
            setIconPadding(a.getDimensionPixelOffset(it, 0))
        }
        a.ifHas(R.styleable.CellView_mc_ellipsize) { setEllipsize(a.getInt(it, 0)) }
        a.ifHas(R.styleable.CellView_maxLines) { labelView.maxLines = a.getInteger(it, 0) }
        a.ifHas(R.styleable.CellView_mc_textBackground) { setTextBackground(a.getDrawable(it)) }
        a.recycle()
    }

    protected fun initView() {
        orientation = VERTICAL
        labelView.textSize = 16f
        labelView.setTextColor(Color.BLACK)
        iconView.scaleType = ImageView.ScaleType.CENTER
        addView(iconView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(labelView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    private fun setTextPadding(a: TypedArray) {
        val start: Int
        val top: Int
        val end: Int
        val bottom: Int
        val padding = a.getDimensionPixelOffset(R.styleable.CellView_mc_textPadding, 0)
        start = a.getDimensionPixelOffset(R.styleable.CellView_mc_textPaddingStart, padding)
        top = a.getDimensionPixelOffset(R.styleable.CellView_mc_textPaddingTop, padding)
        end = a.getDimensionPixelOffset(R.styleable.CellView_mc_textPaddingEnd, padding)
        bottom = a.getDimensionPixelOffset(R.styleable.CellView_mc_textPaddingBottom, padding)
        setTextPadding(start, top, end, bottom)
    }

    fun setIconGravity(gravity: Int) = apply {
        iconGravity = gravity
        removeAllViews()
        when (gravity) {
            Gravity.START -> {
                orientation = HORIZONTAL
                addView(iconView)
                addView(labelView)
            }
            Gravity.TOP -> {
                orientation = VERTICAL
                addView(iconView)
                addView(labelView)
            }
            Gravity.END -> {
                orientation = HORIZONTAL
                addView(labelView)
                addView(iconView)
            }
            Gravity.BOTTOM -> {
                orientation = VERTICAL
                addView(labelView)
                addView(iconView)
            }
        }
    }

    fun setIcon(@DrawableRes drawable: Int) = apply {
        iconView.setImageResource(drawable)
    }

    fun setIcon(drawable: Drawable?) = apply {
        iconView.setImageDrawable(drawable)
    }

    fun setIconWidth(width: Int) = apply {
        iconView.left
        iconView.right
        val layoutParams = iconView.layoutParams
        layoutParams.width = width
        iconView.layoutParams = layoutParams
    }

    fun setIconHeight(height: Int) = apply {
        val layoutParams = iconView.layoutParams
        layoutParams.height = height
        iconView.layoutParams = layoutParams
    }

    fun setIconSize(width: Int, height: Int) = apply {
        val layoutParams = iconView.layoutParams
        layoutParams.width = width
        layoutParams.height = height
        iconView.layoutParams = layoutParams
    }

    fun setIconColor(color: ColorStateList?) = apply {
        iconView.imageTintList = color
    }

    fun setText(@StringRes text: Int) = apply {
        setText(resources.getText(text))
    }

    fun setText(text: CharSequence?) = apply {
        if (text.isNullOrEmpty()) {
            labelView.gone()
        } else {
            labelView.visible()
            labelView.setText(text)
        }
    }

    fun setTextWidth(width: Int) = apply {
        val lp = labelView.layoutParams
        lp.width = width
        labelView.layoutParams = lp
    }

    fun setTextHeight(height: Int) = apply {
        val lp = labelView.layoutParams
        lp.height = height
        labelView.layoutParams = lp
    }

    fun setTextPadding(start: Int, top: Int, end: Int, bottom: Int) = apply {
        labelView.setPadding(start, top, end, bottom)
    }

    fun setTextColor(color: ColorStateList?) = apply {
        labelView.setTextColor(color)
    }

    fun setTextSize(size: Float) = apply {
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
    }

    fun setTextGravity(gravity: Int) = apply {
        labelView.gravity = gravity
    }

    fun setTextBackground(background: Drawable?) = apply {
        labelView.background = background
    }

    fun setTextMaxWidth(width: Int) = apply {
        labelView.maxWidth = width
    }

    fun setTextMinWidth(width: Int) = apply {
        labelView.minWidth = width
    }

    fun setIconPadding(space: Int) = apply {
        val layoutParams = labelView.layoutParams as LayoutParams
        when (iconGravity) {
            Gravity.START -> layoutParams.leftMargin = space
            Gravity.TOP -> layoutParams.topMargin = space
            Gravity.END -> layoutParams.rightMargin = space
            Gravity.BOTTOM -> layoutParams.bottomMargin = space
        }
        labelView.layoutParams = layoutParams
    }

    fun setEllipsize(ellipsize: TextUtils.TruncateAt) = apply {
        labelView.ellipsize = ellipsize
    }

    internal fun setEllipsize(ellipsize: Int) = apply {
        setEllipsize(
            when (ellipsize) {
                1 -> TextUtils.TruncateAt.START
                2 -> TextUtils.TruncateAt.MIDDLE
                3 -> TextUtils.TruncateAt.END
                4 -> TextUtils.TruncateAt.MARQUEE
                else -> TextUtils.TruncateAt.START
            }
        )
    }
}