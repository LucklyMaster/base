package com.master.lib.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.master.lib.ext.ifHas
import com.masterchan.lib.R

/**
 * MenuItem
 * @author MasterChan
 * @date 2021-12-07 14:16
 */
@Suppress("MemberVisibilityCanBePrivate")
class MenuItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    val iconView = ImageView(context)
    val labelView = EditableText(context, false)
    var iconGravity = Gravity.TOP
        private set

    val iconDrawable: Drawable?
        get() {
            return iconView.drawable
        }

    val iconWidth: Int
        get() {
            return iconView.layoutParams.width
        }

    val iconHeight: Int
        get() {
            return iconView.layoutParams.height
        }

    val labelWidth: Int
        get() {
            return iconView.layoutParams.width
        }

    val labelHeight: Int
        get() {
            return iconView.layoutParams.height
        }

    val text: CharSequence
        get() {
            return labelView.text.toString()
        }

    val textSize: Float
        get() {
            return labelView.textSize
        }

    val space: Int
        get() {
            return (labelView.layoutParams as LayoutParams).topMargin
        }

    init {
        orientation = VERTICAL
        labelView.gravity = Gravity.CENTER
        labelView.maxLines = 1
        labelView.inputType = InputType.TYPE_CLASS_TEXT
        iconView.scaleType = ImageView.ScaleType.CENTER
        addView(iconView)
        addView(labelView)

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.MenuItem, defStyleAttr, defStyleRes
        )
        a.ifHas(R.styleable.MenuItem_mc_icon) {
            setIcon(a.getDrawable(R.styleable.MenuItem_mc_icon))
        }
        a.ifHas(R.styleable.MenuItem_mc_icon) {
            setIcon(a.getDrawable(R.styleable.MenuItem_mc_icon))
        }
        a.ifHas(R.styleable.MenuItem_mc_iconColor) {
            setIconColor(a.getColorStateList(R.styleable.MenuItem_mc_iconColor))
        }
        a.ifHas(R.styleable.MenuItem_mc_iconWidth) {
            setIconWidth(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_iconWidth, 0))
        }
        a.ifHas(R.styleable.MenuItem_mc_iconHeight) {
            setIconHeight(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_iconHeight, 0))
        }
        setIconVisible(a.getBoolean(R.styleable.MenuItem_mc_iconVisible, true))
        setTextVisible(a.getBoolean(R.styleable.MenuItem_mc_textVisible, true))
        setTextBackground(a.getDrawable(R.styleable.MenuItem_mc_textBackground))
        a.ifHas(R.styleable.MenuItem_mc_text) {
            setText(a.getString(R.styleable.MenuItem_mc_text)!!)
        }
        a.ifHas(R.styleable.MenuItem_mc_textColor) {
            setTextColor(a.getColorStateList(R.styleable.MenuItem_mc_textColor))
        }
        a.ifHas(R.styleable.MenuItem_mc_textSize) {
            setTextSize(a.getDimension(R.styleable.MenuItem_mc_textSize, 0f))
        }
        a.ifHas(R.styleable.MenuItem_mc_textMaxWidth) {
            setTextMaxWidth(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_textMaxWidth, 0))
        }
        a.ifHas(R.styleable.MenuItem_mc_iconGravity) {
            val gravity = a.getInt(R.styleable.MenuItem_mc_iconGravity, 1)
            setIconGravity(
                when (gravity) {
                    0 -> Gravity.START
                    1 -> Gravity.TOP
                    2 -> Gravity.END
                    3 -> Gravity.BOTTOM
                    else -> Gravity.TOP
                }
            )
        }
        a.ifHas(R.styleable.MenuItem_mc_iconPadding) {
            setIconPadding(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_iconPadding, 0))
        }
        a.ifHas(R.styleable.MenuItem_mc_ellipsize) {
            setEllipsize(a.getInt(R.styleable.MenuItem_mc_ellipsize, 0))
        }
        a.ifHas(R.styleable.MenuItem_mc_editable) {
            labelView.editable = a.getBoolean(R.styleable.MenuItem_mc_editable, false)
        }
        a.recycle()
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
        val layoutParams = iconView.layoutParams as LayoutParams
        layoutParams.width = width
        iconView.layoutParams = layoutParams
    }

    fun setIconHeight(height: Int) = apply {
        val layoutParams = iconView.layoutParams as LayoutParams
        layoutParams.height = height
        iconView.layoutParams = layoutParams
    }

    fun setIconSize(width: Int, height: Int) = apply {
        val layoutParams = iconView.layoutParams as LayoutParams
        layoutParams.width = width
        layoutParams.height = height
        iconView.layoutParams = layoutParams
    }

    fun setIconColor(color: ColorStateList?) = apply {
        iconView.imageTintList = color
    }

    fun setIconVisible(visible: Boolean) = apply {
        iconView.isVisible = visible
    }

    fun setTextVisible(visible: Boolean) = apply {
        labelView.isVisible = visible
    }

    fun setText(@StringRes text: Int) = apply {
        labelView.setText(text)
        labelView.requestLayout()
    }

    fun setText(text: CharSequence) = apply {
        labelView.setText(text)
        labelView.requestLayout()
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