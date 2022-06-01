package com.masterchan.lib.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.masterchan.lib.R
import com.masterchan.lib.ext.activity
import com.masterchan.lib.ext.dp2px

/**
 * @author MasterChan
 * @date 2021-12-09 18:37
 * @describe TitleBar
 */
@Suppress("MemberVisibilityCanBePrivate")
open class TitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_titleBarDefaultStyle,
    defStyleRes: Int = R.style.mc_TitleBar
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    val leftItem = MenuItem(context)
    val middleItem = MenuItem(context)
    val rightItem = MenuItem(context)
    private val mDividerView = TextView(context)
    private var mIconRippleColor = 0
    private var mIconPressedColor = 0
    private var mMiddleItemLayoutGravity = 1
    private var mExcludePaddingHeight = 0

    init {
        layout()

        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.TitleBar, defStyleAttr, defStyleRes
        )

        if (a.hasValue(R.styleable.TitleBar_mc_middleItemLayoutGravity)) {
            val gravity = a.getInt(R.styleable.TitleBar_mc_middleItemLayoutGravity, 1)
            setMiddleItemLayoutGravity(gravity)
        }
        /**icon gravity**/
        if (a.hasValue(R.styleable.TitleBar_mc_iconGravity)) {
            setIconGravity(a.getInt(R.styleable.TitleBar_mc_iconGravity, 0))
        }
        setIconGravity(a, R.styleable.TitleBar_mc_leftIconGravity, leftItem)
        setIconGravity(a, R.styleable.TitleBar_mc_middleIconGravity, middleItem)
        setIconGravity(a, R.styleable.TitleBar_mc_rightIconGravity, rightItem)

        /**icon drawable**/
        setIcon(a, R.styleable.TitleBar_mc_leftIcon, leftItem)
        setIcon(a, R.styleable.TitleBar_mc_middleIcon, middleItem)
        setIcon(a, R.styleable.TitleBar_mc_rightIcon, rightItem)

        /**icon tint & icon color**/
        val useIconTint = a.getBoolean(R.styleable.TitleBar_mc_userIconTint, true)
        if (useIconTint) {
            setIconTintColor(a.getColorStateList(R.styleable.TitleBar_mc_iconColor))
            setIconTintColor(a, R.styleable.TitleBar_mc_leftIconColor, leftItem)
            setIconTintColor(a, R.styleable.TitleBar_mc_middleIconColor, middleItem)
            setIconTintColor(a, R.styleable.TitleBar_mc_rightIconColor, rightItem)
        }

        /**icon width**/
        if (a.hasValue(R.styleable.TitleBar_mc_iconWidth)) {
            setIconWidth(a.getDimensionPixelOffset(R.styleable.TitleBar_mc_iconWidth, -1))
        }
        setIconWidth(a, R.styleable.TitleBar_mc_leftIconWidth, leftItem)
        setIconWidth(a, R.styleable.TitleBar_mc_middleIconWidth, middleItem)
        setIconWidth(a, R.styleable.TitleBar_mc_rightIconWidth, rightItem)

        /**icon height**/
        if (a.hasValue(R.styleable.TitleBar_mc_iconHeight)) {
            setIconWidth(a.getDimensionPixelOffset(R.styleable.TitleBar_mc_iconHeight, -1))
        }
        setIconHeight(a, R.styleable.TitleBar_mc_leftIconHeight, leftItem)
        setIconHeight(a, R.styleable.TitleBar_mc_middleIconHeight, middleItem)
        setIconHeight(a, R.styleable.TitleBar_mc_rightIconHeight, rightItem)

        /**icon padding**/
        if (a.hasValue(R.styleable.TitleBar_mc_iconPadding)) {
            setIconPadding(a.getDimensionPixelOffset(R.styleable.TitleBar_mc_iconPadding, 0))
        }
        setIconPadding(a, R.styleable.TitleBar_mc_leftIconPadding, leftItem)
        setIconPadding(a, R.styleable.TitleBar_mc_middleIconPadding, middleItem)
        setIconPadding(a, R.styleable.TitleBar_mc_rightIconPadding, rightItem)

        /**icon active**/
        val userRipple = a.getBoolean(R.styleable.TitleBar_mc_iconUseRipple, true)
        mIconRippleColor = a.getColor(R.styleable.TitleBar_mc_iconRippleColor, 0)
        mIconPressedColor = a.getColor(R.styleable.TitleBar_mc_iconPressedColor, 0)
        setIconPressedEffect(userRipple)

        /**text**/
        setText(a, R.styleable.TitleBar_mc_leftText, leftItem)
        setText(a, R.styleable.TitleBar_mc_middleText, middleItem)
        setText(a, R.styleable.TitleBar_mc_rightText, rightItem)

        /**text gravity**/
        if (a.hasValue(R.styleable.TitleBar_mc_textGravity)) {
            setTextGravity(a.getInt(R.styleable.TitleBar_mc_textGravity, 0))
        }
        setTextGravity(a, R.styleable.TitleBar_mc_leftTextGravity, leftItem)
        setTextGravity(a, R.styleable.TitleBar_mc_middleTextGravity, middleItem)
        setTextGravity(a, R.styleable.TitleBar_mc_rightTextGravity, rightItem)

        /**text color**/
        if (a.hasValue(R.styleable.TitleBar_mc_textColor)) {
            setTextColor(a.getColorStateList(R.styleable.TitleBar_mc_textColor))
        }
        setTextColor(a, R.styleable.TitleBar_mc_leftTextColor, leftItem)
        setTextColor(a, R.styleable.TitleBar_mc_middleTextColor, middleItem)
        setTextColor(a, R.styleable.TitleBar_mc_rightTextColor, rightItem)

        /**text size**/
        if (a.hasValue(R.styleable.TitleBar_mc_textSize)) {
            setTextSize(a.getDimension(R.styleable.TitleBar_mc_textSize, dp2px(16f)))
        }
        setTextSize(a, R.styleable.TitleBar_mc_leftTextSize, leftItem)
        setTextSize(a, R.styleable.TitleBar_mc_middleTextSize, middleItem)
        setTextSize(a, R.styleable.TitleBar_mc_rightTextSize, rightItem)

        /**text hint**/
        if (a.hasValue(R.styleable.TitleBar_mc_hintTextColor)) {
            setHintTextColor(a.getColor(R.styleable.TitleBar_mc_hintTextColor, 0))
        }
        setHintText(a, R.styleable.TitleBar_mc_leftTextHint, leftItem)
        setHintText(a, R.styleable.TitleBar_mc_middleTextHint, middleItem)
        setHintText(a, R.styleable.TitleBar_mc_rightTextHint, rightItem)

        /**text ellipsize**/
        if (a.hasValue(R.styleable.TitleBar_mc_ellipsize)) {
            setTextEllipsize(a.getInt(R.styleable.TitleBar_mc_ellipsize, 0))
        }

        /**text max width**/
        if (a.hasValue(R.styleable.TitleBar_mc_textMaxWidth)) {
            setTextMaxWidth(a.getDimensionPixelOffset(R.styleable.TitleBar_mc_textMaxWidth, 0))
        }
        setTextMaxWidth(a, R.styleable.TitleBar_mc_leftTextMaxWidth, leftItem)
        setTextMaxWidth(a, R.styleable.TitleBar_mc_middleTextMaxWidth, middleItem)
        setTextMaxWidth(a, R.styleable.TitleBar_mc_rightTextMaxWidth, rightItem)

        /**text min width**/
        if (a.hasValue(R.styleable.TitleBar_mc_textMinWidth)) {
            setTextMinWidth(a.getDimensionPixelOffset(R.styleable.TitleBar_mc_textMinWidth, 0))
        }
        setTextMinWidth(a, R.styleable.TitleBar_mc_leftTextMinWidth, leftItem)
        setTextMinWidth(a, R.styleable.TitleBar_mc_middleTextMinWidth, middleItem)
        setTextMinWidth(a, R.styleable.TitleBar_mc_rightTextMinWidth, rightItem)

        /**left item margin**/
        val leftStart = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_leftMarginStart, 0)
        val leftTop = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_leftMarginTop, 0)
        val leftBottom = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_leftMarginBottom, 0)
        setLeftItemMargin(leftStart, leftTop, leftBottom)

        /**middle item margin**/
        val middleStart = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_middleMarginStart, 0)
        val middleTop = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_middleMarginTop, 0)
        val middleEnd = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_middleMarginEnd, 0)
        val middleBottom = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_middleMarginBottom, 0)
        setMiddleItemMargin(middleStart, middleTop, middleEnd, middleBottom)

        /**right item margin**/
        val rightTop = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_rightMarginTop, 0)
        val rightEnd = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_rightMarginEnd, 0)
        val rightBottom = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_rightMarginBottom, 0)
        setRightItemMargin(rightTop, rightEnd, rightBottom)

        /**divider**/
        setDividerVisible(a.getBoolean(R.styleable.TitleBar_mc_dividerVisible, false))
        if (a.hasValue(R.styleable.TitleBar_mc_dividerColor)) {
            setDividerColor(a.getColor(R.styleable.TitleBar_mc_dividerColor, 0))
        }
        setDividerHeight(a.getDimensionPixelOffset(R.styleable.TitleBar_mc_dividerHeight, 0))
        val marginStart = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_dividerMarginStart, 0)
        val marginEnd = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_dividerMarginEnd, 0)
        setDividerMargin(marginStart, marginEnd)

        mExcludePaddingHeight = a.getDimension(
            R.styleable.TitleBar_mc_excludePaddingHeight, dp2px(50f)
        ).toInt()

        if (a.hasValue(R.styleable.TitleBar_android_background)) {
            background = a.getDrawable(R.styleable.TitleBar_android_background)
        }
        setClickLeftFinish(a.getBoolean(R.styleable.TitleBar_mc_clickLeftFinish, true))

        a.recycle()
    }

    private fun layout() {
        leftItem.id = Int.MAX_VALUE - 1000
        middleItem.id = Int.MAX_VALUE - 1001
        rightItem.id = Int.MAX_VALUE - 1002
        mDividerView.id = Int.MAX_VALUE - 1003

        addView(leftItem, LayoutParams(LayoutParams.WRAP_CONTENT, 0))
        addView(middleItem, LayoutParams(LayoutParams.WRAP_CONTENT, 0))
        addView(rightItem, LayoutParams(LayoutParams.WRAP_CONTENT, 0))
        addView(mDividerView, LayoutParams(ConstraintSet.UNSET, 10))

        val set = ConstraintSet()
        val parentId = ConstraintSet.PARENT_ID
        set.clone(this)
        set.connect(leftItem.id, ConstraintSet.START, parentId, ConstraintSet.START)
        set.connect(leftItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        set.connect(leftItem.id, ConstraintSet.BOTTOM, mDividerView.id, ConstraintSet.TOP)

        set.connect(middleItem.id, ConstraintSet.START, parentId, ConstraintSet.START)
        set.connect(middleItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        set.connect(middleItem.id, ConstraintSet.BOTTOM, mDividerView.id, ConstraintSet.TOP)
        set.connect(middleItem.id, ConstraintSet.END, parentId, ConstraintSet.END)

        set.connect(rightItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        set.connect(rightItem.id, ConstraintSet.END, parentId, ConstraintSet.END)
        set.connect(rightItem.id, ConstraintSet.BOTTOM, mDividerView.id, ConstraintSet.TOP)

        set.connect(mDividerView.id, ConstraintSet.START, parentId, ConstraintSet.START)
        set.connect(mDividerView.id, ConstraintSet.END, parentId, ConstraintSet.END)
        set.connect(mDividerView.id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)
        set.applyTo(this)
    }

    fun setMiddleItemLayoutGravity(@IntRange(from = 0, to = 2) gravity: Int) {
        if (gravity == mMiddleItemLayoutGravity) {
            return
        }
        mMiddleItemLayoutGravity = gravity
        val set = ConstraintSet()
        set.clone(this)
        val parentId = ConstraintSet.PARENT_ID
        set.clear(middleItem.id, ConstraintSet.START)
        set.clear(middleItem.id, ConstraintSet.END)
        when (gravity) {
            0 -> {
                set.connect(middleItem.id, ConstraintSet.START, leftItem.id, ConstraintSet.END)
            }
            1 -> {
                set.connect(middleItem.id, ConstraintSet.START, parentId, ConstraintSet.START)
                set.connect(middleItem.id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)
            }
            2 -> {
                set.connect(middleItem.id, ConstraintSet.END, rightItem.id, ConstraintSet.START)
            }
        }
        set.applyTo(this)
    }

    private fun setIconGravity(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setIconGravity(getIconGravity(a.getInt(styleable, 0)))
        }
    }

    private fun getIconGravity(value: Int): Int {
        return when (value) {
            0 -> Gravity.START
            1 -> Gravity.TOP
            2 -> Gravity.END
            3 -> Gravity.BOTTOM
            else -> Gravity.TOP
        }
    }

    private fun getTextGravity(value: Int): Int {
        return when (value) {
            0 -> Gravity.START or Gravity.CENTER_VERTICAL
            1 -> Gravity.END or Gravity.CENTER_VERTICAL
            2 -> Gravity.CENTER or Gravity.CENTER_VERTICAL
            else -> Gravity.START or Gravity.CENTER_VERTICAL
        }
    }

    private fun setIcon(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setIcon(a.getDrawable(styleable))
        } else {
            item.iconView.visibility = GONE
        }
    }

    private fun setIconTintColor(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setIconColor(a.getColorStateList(styleable))
        }
    }

    private fun setIconWidth(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setIconWidth(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setIconHeight(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setIconHeight(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setText(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setText(a.getString(styleable)!!)
        }
    }

    private fun setTextGravity(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.labelView.gravity = getTextGravity(a.getInt(styleable, 0))
        }
    }

    private fun setTextSize(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setTextSize(a.getDimension(styleable, 0f))
        }
    }

    private fun setHintText(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.labelView.hint = a.getString(styleable)
        }
    }

    private fun setTextColor(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setTextColor(a.getColorStateList(styleable))
        }
    }

    private fun setTextMaxWidth(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setTextMaxWidth(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setTextMinWidth(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setTextMinWidth(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setIconPadding(a: TypedArray, styleable: Int, item: MenuItem) {
        if (a.hasValue(styleable)) {
            item.setIconPadding(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    fun setIconGravity(gravity: Int): TitleBar {
        val gravityValue = getIconGravity(gravity)
        leftItem.setIconGravity(gravityValue)
        middleItem.setIconGravity(gravityValue)
        rightItem.setIconGravity(gravityValue)
        return this
    }

    fun setIconTintColor(color: ColorStateList?): TitleBar {
        leftItem.setIconColor(color)
        middleItem.setIconColor(color)
        rightItem.setIconColor(color)
        return this
    }

    fun setIconWidth(width: Int): TitleBar {
        leftItem.setIconWidth(width)
        middleItem.setIconWidth(width)
        rightItem.setIconWidth(width)
        return this
    }

    fun setIconHeight(height: Int): TitleBar {
        leftItem.setIconHeight(height)
        middleItem.setIconHeight(height)
        rightItem.setIconHeight(height)
        return this
    }

    fun setIconPressedEffect(useRipple: Boolean): TitleBar {
        if (useRipple) {
            setIconRippleColor(mIconRippleColor)
        } else {
            setIconPressedColor(mIconPressedColor)
        }
        return this
    }

    fun setIconRippleColor(@ColorInt color: Int): TitleBar {
        val mask = GradientDrawable()
        mask.shape = GradientDrawable.OVAL
        mask.color = ColorStateList.valueOf(color)
        leftItem.iconView.background = RippleDrawable(ColorStateList.valueOf(color), null, mask)
        middleItem.iconView.background = RippleDrawable(ColorStateList.valueOf(color), null, mask)
        rightItem.iconView.background = RippleDrawable(ColorStateList.valueOf(color), null, mask)
        return this
    }

    fun setIconPressedColor(@ColorInt pressedColor: Int): TitleBar {
        setLeftIconDrawable(leftItem.iconView.background, ColorDrawable(pressedColor))
        setMiddleIconDrawable(middleItem.iconView.background, ColorDrawable(pressedColor))
        setRightIconDrawable(rightItem.iconView.background, ColorDrawable(pressedColor))
        return this
    }

    fun setLeftIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?): TitleBar {
        val drawable = StateListDrawable()
        val normalState = intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed)
        val pressedState = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
        if (normalDrawable != null) {
            drawable.addState(normalState, normalDrawable)
        }
        if (pressedDrawable != null) {
            drawable.addState(pressedState, pressedDrawable)
        }
        leftItem.iconView.background = drawable
        return this
    }

    fun setMiddleIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?): TitleBar {
        val drawable = StateListDrawable()
        val normalState = intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed)
        val pressedState = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
        if (normalDrawable != null) {
            drawable.addState(normalState, normalDrawable)
        }
        if (pressedDrawable != null) {
            drawable.addState(pressedState, pressedDrawable)
        }
        middleItem.iconView.background = drawable
        return this
    }

    fun setRightIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?): TitleBar {
        val drawable = StateListDrawable()
        val normalState = intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed)
        val pressedState = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
        if (normalDrawable != null) {
            drawable.addState(normalState, normalDrawable)
        }
        if (pressedDrawable != null) {
            drawable.addState(pressedState, pressedDrawable)
        }
        rightItem.iconView.background = drawable
        return this
    }

    fun setTextGravity(gravity: Int): TitleBar {
        val gravityValue = getTextGravity(gravity)
        leftItem.labelView.gravity = gravityValue
        middleItem.labelView.gravity = gravityValue
        rightItem.labelView.gravity = gravityValue
        return this
    }

    fun setTextColor(textColor: ColorStateList?): TitleBar {
        leftItem.setTextColor(textColor)
        middleItem.setTextColor(textColor)
        rightItem.setTextColor(textColor)
        return this
    }

    fun setTextSize(textSize: Float): TitleBar {
        leftItem.setTextSize(textSize)
        middleItem.setTextSize(textSize)
        rightItem.setTextSize(textSize)
        return this
    }

    fun setHintTextColor(textColor: Int): TitleBar {
        leftItem.labelView.setHintTextColor(textColor)
        middleItem.labelView.setHintTextColor(textColor)
        rightItem.labelView.setHintTextColor(textColor)
        return this
    }

    fun setTextMaxWidth(maxWidth: Int): TitleBar {
        leftItem.setTextMaxWidth(maxWidth)
        middleItem.setTextMaxWidth(maxWidth)
        rightItem.setTextMaxWidth(maxWidth)
        return this
    }

    fun setTextMinWidth(minWidth: Int): TitleBar {
        leftItem.setTextMinWidth(minWidth)
        middleItem.setTextMinWidth(minWidth)
        rightItem.setTextMinWidth(minWidth)
        return this
    }

    fun setTextEllipsize(ellipsize: TextUtils.TruncateAt): TitleBar {
        leftItem.setEllipsize(ellipsize)
        middleItem.setEllipsize(ellipsize)
        rightItem.setEllipsize(ellipsize)
        return this
    }

    fun setTextEllipsize(ellipsize: Int): TitleBar {
        leftItem.setEllipsize(ellipsize)
        middleItem.setEllipsize(ellipsize)
        rightItem.setEllipsize(ellipsize)
        return this
    }

    fun setLeftItemMargin(left: Int, top: Int, bottom: Int): TitleBar {
        val layoutParams = leftItem.layoutParams as LayoutParams
        layoutParams.marginStart = left
        layoutParams.topMargin = top
        layoutParams.bottomMargin = bottom
        leftItem.layoutParams = layoutParams
        return this
    }

    fun setMiddleItemMargin(start: Int, top: Int, end: Int, bottom: Int): TitleBar {
        val layoutParams = middleItem.layoutParams as LayoutParams
        layoutParams.marginStart = start
        layoutParams.topMargin = top
        layoutParams.marginEnd = end
        layoutParams.bottomMargin = bottom
        middleItem.layoutParams = layoutParams
        return this
    }

    fun setRightItemMargin(top: Int, end: Int, bottom: Int): TitleBar {
        val layoutParams = rightItem.layoutParams as LayoutParams
        layoutParams.topMargin = top
        layoutParams.marginEnd = end
        layoutParams.bottomMargin = bottom
        rightItem.layoutParams = layoutParams
        return this
    }

    fun setIconPadding(padding: Int): TitleBar {
        leftItem.setIconPadding(padding)
        middleItem.setIconPadding(padding)
        rightItem.setIconPadding(padding)
        return this
    }

    fun setDividerVisible(visible: Boolean): TitleBar {
        mDividerView.visibility = if (visible) VISIBLE else GONE
        return this
    }

    fun setDividerColor(@ColorInt color: Int): TitleBar {
        mDividerView.setBackgroundColor(color)
        return this
    }

    fun setDividerHeight(height: Int): TitleBar {
        val layoutParams = mDividerView.layoutParams as LayoutParams
        layoutParams.height = height
        mDividerView.layoutParams = layoutParams
        return this
    }

    fun setDividerMargin(marginStart: Int, marginEnd: Int): TitleBar {
        val layoutParams = mDividerView.layoutParams as LayoutParams
        layoutParams.marginStart = marginStart
        layoutParams.marginEnd = marginEnd
        mDividerView.layoutParams = layoutParams
        return this
    }

    fun setClickLeftFinish(finish: Boolean): TitleBar {
        if (!finish) {
            leftItem.iconView.setOnClickListener(null)
            leftItem.labelView.setOnClickListener(null)
            return this
        }
        if (leftItem.iconDrawable != null) {
            leftItem.iconView.setOnClickListener { activity?.finish() }
            return this
        }
        if (leftItem.text.isNotEmpty()) {
            leftItem.labelView.setOnClickListener { activity?.finish() }
        }
        return this
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasure = heightMeasureSpec
        val mode = MeasureSpec.getMode(heightMeasure)
        if (mode != MeasureSpec.EXACTLY) {
            val height = mExcludePaddingHeight + paddingTop + paddingBottom
            heightMeasure = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasure)
    }
}