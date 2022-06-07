package com.masterchan.lib.view

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

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        labelView.gravity = Gravity.CENTER
        labelView.maxLines = 1
        labelView.inputType = InputType.TYPE_CLASS_TEXT
        iconView.scaleType = ImageView.ScaleType.CENTER
        addView(iconView)
        addView(labelView)

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MenuItem,
            defStyleAttr,
            defStyleRes
        )
        if (a.hasValue(R.styleable.MenuItem_mc_icon)) {
            setIcon(a.getDrawable(R.styleable.MenuItem_mc_icon))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_iconColor)) {
            setIconColor(a.getColorStateList(R.styleable.MenuItem_mc_iconColor))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_iconWidth)) {
            setIconWidth(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_iconWidth, 0))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_iconHeight)) {
            setIconHeight(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_iconHeight, 0))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_text)) {
            setText(a.getString(R.styleable.MenuItem_mc_text)!!)
        }
        if (a.hasValue(R.styleable.MenuItem_mc_textColor)) {
            setTextColor(a.getColorStateList(R.styleable.MenuItem_mc_textColor))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_textSize)) {
            setTextSize(a.getDimension(R.styleable.MenuItem_mc_textSize, 0f))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_textMaxWidth)) {
            setTextMaxWidth(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_textMaxWidth, 0))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_iconGravity)) {
            val gravity = a.getInt(R.styleable.MenuItem_mc_iconGravity, 0)
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
        if (a.hasValue(R.styleable.MenuItem_mc_iconPadding)) {
            setIconPadding(a.getDimensionPixelOffset(R.styleable.MenuItem_mc_iconPadding, 0))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_ellipsize)) {
            setEllipsize(a.getInt(R.styleable.MenuItem_mc_ellipsize, 0))
        }
        if (a.hasValue(R.styleable.MenuItem_mc_editable)) {
            labelView.editable = a.getBoolean(R.styleable.MenuItem_mc_editable, false)
        }
        a.recycle()
    }

    fun setIconGravity(gravity: Int): MenuItem {
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
        return this
    }

    fun setIcon(@DrawableRes drawable: Int): MenuItem {
        iconView.setImageResource(drawable)
        return this
    }

    fun setIcon(drawable: Drawable?): MenuItem {
        iconView.setImageDrawable(drawable)
        return this
    }

    fun setIconWidth(width: Int): MenuItem {
        val layoutParams = iconView.layoutParams as LayoutParams
        layoutParams.width = width
        iconView.layoutParams = layoutParams
        return this
    }

    fun setIconHeight(height: Int): MenuItem {
        val layoutParams = iconView.layoutParams as LayoutParams
        layoutParams.height = height
        iconView.layoutParams = layoutParams
        return this
    }

    fun setIconSize(width: Int, height: Int): MenuItem {
        val layoutParams = iconView.layoutParams as LayoutParams
        layoutParams.width = width
        layoutParams.height = height
        iconView.layoutParams = layoutParams
        return this
    }

    fun setIconColor(color: ColorStateList?): MenuItem {
        iconView.imageTintList = color
        return this
    }

    fun setText(@StringRes text: Int): MenuItem {
        labelView.setText(text)
        labelView.requestLayout()
        return this
    }

    fun setText(text: CharSequence): MenuItem {
        labelView.setText(text)
        labelView.requestLayout()
        return this
    }

    fun setTextColor(color: ColorStateList?): MenuItem {
        labelView.setTextColor(color)
        return this
    }

    fun setTextSize(size: Float): MenuItem {
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        return this
    }

    fun setTextMaxWidth(width: Int): MenuItem {
        labelView.maxWidth = width
        return this
    }

    fun setTextMinWidth(width: Int): MenuItem {
        labelView.minWidth = width
        return this
    }

    fun setIconPadding(space: Int): MenuItem {
        val layoutParams = labelView.layoutParams as LayoutParams
        when (iconGravity) {
            Gravity.START -> layoutParams.leftMargin = space
            Gravity.TOP -> layoutParams.topMargin = space
            Gravity.END -> layoutParams.rightMargin = space
            Gravity.BOTTOM -> layoutParams.bottomMargin = space
        }
        labelView.layoutParams = layoutParams
        return this
    }

    fun setEllipsize(ellipsize: TextUtils.TruncateAt): MenuItem {
        labelView.ellipsize = ellipsize
        return this
    }

    fun setEllipsize(ellipsize: Int): MenuItem {
        setEllipsize(
            when (ellipsize) {
                1 -> TextUtils.TruncateAt.START
                2 -> TextUtils.TruncateAt.MIDDLE
                3 -> TextUtils.TruncateAt.END
                4 -> TextUtils.TruncateAt.MARQUEE
                else -> TextUtils.TruncateAt.START
            }
        )
        return this
    }

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
}