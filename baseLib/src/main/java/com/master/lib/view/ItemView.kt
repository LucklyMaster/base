package com.master.lib.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.GravityInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.master.R
import com.master.lib.ext.dp2pxi
import com.master.lib.ext.getColor
import com.master.lib.ext.setPaddingBottom
import kotlin.math.max

/**
 * ItemView
 * @author: MasterChan
 * @date: 2022-7-11 21:48
 */
@Suppress("MemberVisibilityCanBePrivate", "LeakingThis")
open class ItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_itemViewDefaultStyle,
    defStyleRes: Int = R.style.mc_ItemView
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    open val leftItem = CellView(context)
    open val middleItem = CellView(context)
    open val rightItem = CellView(context)
    protected var iconRippleColor = 0
    protected var iconPressedColor = 0

    /**
     * 除去Padding之后的高度
     * 此字段只有在[android:layout_height="wrap_content"]时生效，使用此字段后，View的实际
     * 高度为[excludePaddingHeight]+[getPaddingBottom]+[getPaddingTop]
     */
    protected var excludePaddingHeight = 0

    protected var dividerVisible = false
    protected var dividerHeight = 0
    protected var dividerColor: Int = 0
    protected var dividerMarginStart = 0
    protected var dividerMarginEnd = 0
    protected var dividerMarginColor = 0
    protected val dividerPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    init {
        initView()

        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ItemView, defStyleAttr, defStyleRes
        )

        setMiddleItemLayoutGravity(a.getInt(R.styleable.ItemView_mc_middleItemLayoutGravity, 0))
        /**icon gravity**/
        if (a.hasValue(R.styleable.ItemView_mc_iconGravity)) {
            setIconGravity(a.getInt(R.styleable.ItemView_mc_iconGravity, 0))
        }
        setIconGravity(a, R.styleable.ItemView_mc_leftIconGravity, leftItem)
        setIconGravity(a, R.styleable.ItemView_mc_middleIconGravity, middleItem)
        setIconGravity(a, R.styleable.ItemView_mc_rightIconGravity, rightItem)

        /**icon drawable**/
        setIcon(a, R.styleable.ItemView_mc_leftIcon, leftItem)
        setIcon(a, R.styleable.ItemView_mc_middleIcon, middleItem)
        setIcon(a, R.styleable.ItemView_mc_rightIcon, rightItem)

        /**icon tint & icon color**/
        val useIconTint = a.getBoolean(R.styleable.ItemView_mc_useIconTint, true)
        if (useIconTint) {
            setIconTintColor(a.getColorStateList(R.styleable.ItemView_mc_iconColor))
            setIconTintColor(a, R.styleable.ItemView_mc_leftIconColor, leftItem)
            setIconTintColor(a, R.styleable.ItemView_mc_middleIconColor, middleItem)
            setIconTintColor(a, R.styleable.ItemView_mc_rightIconColor, rightItem)
        }

        /**icon width**/
        if (a.hasValue(R.styleable.ItemView_mc_iconWidth)) {
            setIconWidth(a.getLayoutDimension(R.styleable.ItemView_mc_iconWidth, -1))
        }
        setIconWidth(a, R.styleable.ItemView_mc_leftIconWidth, leftItem)
        setIconWidth(a, R.styleable.ItemView_mc_middleIconWidth, middleItem)
        setIconWidth(a, R.styleable.ItemView_mc_rightIconWidth, rightItem)

        /**icon height**/
        if (a.hasValue(R.styleable.ItemView_mc_iconHeight)) {
            setIconWidth(a.getLayoutDimension(R.styleable.ItemView_mc_iconHeight, -1))
        }
        setIconHeight(a, R.styleable.ItemView_mc_leftIconHeight, leftItem)
        setIconHeight(a, R.styleable.ItemView_mc_middleIconHeight, middleItem)
        setIconHeight(a, R.styleable.ItemView_mc_rightIconHeight, rightItem)

        /**icon padding**/
        if (a.hasValue(R.styleable.ItemView_mc_iconPadding)) {
            setIconPadding(a.getDimensionPixelOffset(R.styleable.ItemView_mc_iconPadding, 0))
        }
        setIconPadding(a, R.styleable.ItemView_mc_leftIconPadding, leftItem)
        setIconPadding(a, R.styleable.ItemView_mc_middleIconPadding, middleItem)
        setIconPadding(a, R.styleable.ItemView_mc_rightIconPadding, rightItem)

        /**icon active**/
        val userRipple = a.getBoolean(R.styleable.ItemView_mc_iconUseRipple, true)
        iconRippleColor = a.getColor(R.styleable.ItemView_mc_iconRippleColor, 0)
        iconPressedColor = a.getColor(R.styleable.ItemView_mc_iconPressedColor, 0)
        setIconPressedEffect(userRipple)

        /**text**/
        setText(a, R.styleable.ItemView_mc_leftText, leftItem)
        setText(a, R.styleable.ItemView_mc_middleText, middleItem)
        setText(a, R.styleable.ItemView_mc_rightText, rightItem)

        /**text size**/
        setTextWidth(a, R.styleable.ItemView_mc_leftTextWidth, leftItem)
        setTextHeight(a, R.styleable.ItemView_mc_leftTextHeight, leftItem)
        setTextWidth(a, R.styleable.ItemView_mc_middleTextWidth, middleItem)
        setTextHeight(a, R.styleable.ItemView_mc_middleTextHeight, middleItem)
        setTextWidth(a, R.styleable.ItemView_mc_rightTextWidth, rightItem)
        setTextHeight(a, R.styleable.ItemView_mc_rightTextHeight, rightItem)

        /**text gravity**/
        if (a.hasValue(R.styleable.ItemView_mc_textGravity)) {
            setTextGravity(a.getInt(R.styleable.ItemView_mc_textGravity, 0))
        }
        setTextGravity(a, R.styleable.ItemView_mc_leftTextGravity, leftItem)
        setTextGravity(a, R.styleable.ItemView_mc_middleTextGravity, middleItem)
        setTextGravity(a, R.styleable.ItemView_mc_rightTextGravity, rightItem)

        /**text color**/
        if (a.hasValue(R.styleable.ItemView_mc_textColor)) {
            setTextColor(a.getColorStateList(R.styleable.ItemView_mc_textColor))
        }
        setTextColor(a, R.styleable.ItemView_mc_leftTextColor, leftItem)
        setTextColor(a, R.styleable.ItemView_mc_middleTextColor, middleItem)
        setTextColor(a, R.styleable.ItemView_mc_rightTextColor, rightItem)

        /**text background**/
        setTextBackground(a, R.styleable.ItemView_mc_leftTextBackground, leftItem)
        setTextBackground(a, R.styleable.ItemView_mc_middleTextBackground, middleItem)
        setTextBackground(a, R.styleable.ItemView_mc_rightTextBackground, rightItem)

        /**text size**/
        if (a.hasValue(R.styleable.ItemView_mc_textSize)) {
            setTextSize(a.getDimension(R.styleable.ItemView_mc_textSize, 0f))
        }
        setTextSize(a, R.styleable.ItemView_mc_leftTextSize, leftItem)
        setTextSize(a, R.styleable.ItemView_mc_middleTextSize, middleItem)
        setTextSize(a, R.styleable.ItemView_mc_rightTextSize, rightItem)

        /**text padding**/
        setTextPadding(a)

        /**text hint**/
        if (a.hasValue(R.styleable.ItemView_mc_hintTextColor)) {
            setHintTextColor(a.getColor(R.styleable.ItemView_mc_hintTextColor, 0))
        }
        setHintText(a, R.styleable.ItemView_mc_leftTextHint, leftItem)
        setHintText(a, R.styleable.ItemView_mc_middleTextHint, middleItem)
        setHintText(a, R.styleable.ItemView_mc_rightTextHint, rightItem)

        /**text ellipsize**/
        if (a.hasValue(R.styleable.ItemView_mc_ellipsize)) {
            setTextEllipsize(a.getInt(R.styleable.ItemView_mc_ellipsize, 0))
        }

        /**text max width**/
        if (a.hasValue(R.styleable.ItemView_mc_textMaxWidth)) {
            setTextMaxWidth(a.getDimensionPixelOffset(R.styleable.ItemView_mc_textMaxWidth, 0))
        }
        setTextMaxWidth(a, R.styleable.ItemView_mc_leftTextMaxWidth, leftItem)
        setTextMaxWidth(a, R.styleable.ItemView_mc_middleTextMaxWidth, middleItem)
        setTextMaxWidth(a, R.styleable.ItemView_mc_rightTextMaxWidth, rightItem)

        /**text min width**/
        if (a.hasValue(R.styleable.ItemView_mc_textMinWidth)) {
            setTextMinWidth(a.getDimensionPixelOffset(R.styleable.ItemView_mc_textMinWidth, 0))
        }
        setTextMinWidth(a, R.styleable.ItemView_mc_leftTextMinWidth, leftItem)
        setTextMinWidth(a, R.styleable.ItemView_mc_middleTextMinWidth, middleItem)
        setTextMinWidth(a, R.styleable.ItemView_mc_rightTextMinWidth, rightItem)

        /**left item margin**/
        val leftStart = a.getDimensionPixelOffset(R.styleable.ItemView_mc_leftMarginStart, 0)
        val leftTop = a.getDimensionPixelOffset(R.styleable.ItemView_mc_leftMarginTop, 0)
        val leftBottom = a.getDimensionPixelOffset(R.styleable.ItemView_mc_leftMarginBottom, 0)
        setLeftItemMargin(leftStart, leftTop, leftBottom)

        /**middle item margin**/
        val middleStart = a.getDimensionPixelOffset(R.styleable.ItemView_mc_middleMarginStart, 0)
        val middleTop = a.getDimensionPixelOffset(R.styleable.ItemView_mc_middleMarginTop, 0)
        val middleEnd = a.getDimensionPixelOffset(R.styleable.ItemView_mc_middleMarginEnd, 0)
        val middleBottom = a.getDimensionPixelOffset(R.styleable.ItemView_mc_middleMarginBottom, 0)
        setMiddleItemMargin(middleStart, middleTop, middleEnd, middleBottom)

        /**right item margin**/
        val rightTop = a.getDimensionPixelOffset(R.styleable.ItemView_mc_rightMarginTop, 0)
        val rightEnd = a.getDimensionPixelOffset(R.styleable.ItemView_mc_rightMarginEnd, 0)
        val rightBottom = a.getDimensionPixelOffset(R.styleable.ItemView_mc_rightMarginBottom, 0)
        setRightItemMargin(rightTop, rightEnd, rightBottom)

        /**divider**/
        dividerVisible = a.getBoolean(R.styleable.ItemView_mc_dividerVisible, false)
        dividerColor = a.getColor(R.styleable.ItemView_mc_dividerColor, "#D6D6D6")
        dividerMarginColor = a.getColor(R.styleable.ItemView_mc_dividerMarginColor, "#00000000")
        dividerHeight = a.getDimensionPixelOffset(R.styleable.ItemView_mc_dividerHeight, 0)
        dividerMarginStart = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_dividerMarginStart, 0
        )
        dividerMarginEnd = a.getDimensionPixelOffset(R.styleable.ItemView_mc_dividerMarginEnd, 0)
        setPaddingBottom()

        excludePaddingHeight = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_excludePaddingHeight, context.dp2pxi(50f)
        )
        a.recycle()
    }

    protected open fun initView() {
        leftItem.id = Int.MAX_VALUE - 1001
        middleItem.id = Int.MAX_VALUE - 1002
        rightItem.id = Int.MAX_VALUE - 1003

        addView(leftItem, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        addView(middleItem, LayoutParams(0, LayoutParams.WRAP_CONTENT))
        addView(rightItem, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        val sets = ConstraintSet()
        val parentId = ConstraintSet.PARENT_ID
        sets.clone(this)

        sets.connect(leftItem.id, ConstraintSet.START, parentId, ConstraintSet.START)
        sets.connect(leftItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        sets.connect(leftItem.id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)

        sets.connect(middleItem.id, ConstraintSet.START, leftItem.id, ConstraintSet.END)
        sets.connect(middleItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        sets.connect(middleItem.id, ConstraintSet.END, rightItem.id, ConstraintSet.START)
        sets.connect(middleItem.id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)

        sets.connect(rightItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        sets.connect(rightItem.id, ConstraintSet.END, parentId, ConstraintSet.END)
        sets.connect(rightItem.id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)

        sets.applyTo(this)
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)
        if (dividerVisible && dividerHeight > 0) {
            dividerPaint.strokeWidth = dividerHeight.toFloat()
            dividerPaint.color = dividerColor
            val y = height.toFloat() - dividerHeight / 2f
            canvas.drawLine(
                dividerMarginStart.toFloat(), y, width.toFloat() - dividerMarginEnd, y, dividerPaint
            )
            dividerPaint.color = dividerMarginColor
            canvas.drawLine(0f, y, dividerMarginStart.toFloat(), y, dividerPaint)
            canvas.drawLine(
                (width - dividerMarginEnd).toFloat(), y, width.toFloat(), y, dividerPaint
            )
        }
    }

    private fun setIconGravity(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIconGravity(a.getInt(styleable, 0))
        }
    }

    private fun setIcon(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIcon(a.getDrawable(styleable))
        } else {
            item.iconView.visibility = GONE
        }
    }

    private fun setIconTintColor(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIconColor(a.getColorStateList(styleable))
        }
    }

    private fun setIconWidth(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIconWidth(a.getLayoutDimension(styleable, 0))
        }
    }

    private fun setIconHeight(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIconHeight(a.getLayoutDimension(styleable, 0))
        }
    }

    private fun setText(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setText(a.getString(styleable)!!)
        }
    }

    private fun setTextWidth(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextWidth(a.getLayoutDimension(styleable, 0))
        }
    }

    private fun setTextHeight(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextHeight(a.getLayoutDimension(styleable, 0))
        }
    }

    private fun setTextGravity(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.labelView.gravity = a.getInt(styleable, 0)
        }
    }

    private fun setTextSize(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextSize(a.getDimension(styleable, 0f))
        }
    }

    private fun setTextPadding(a: TypedArray) {
        var start: Int
        var top: Int
        var end: Int
        var bottom: Int
        var padding = a.getDimensionPixelOffset(R.styleable.ItemView_mc_leftTextPadding, 0)
        start = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_leftTextPaddingStart, padding
        )
        top = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_leftTextPaddingTop, padding
        )
        end = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_leftTextPaddingEnd, padding
        )
        bottom = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_leftTextPaddingBottom, padding
        )
        setTextPadding(leftItem, start, top, end, bottom)

        padding = a.getDimensionPixelOffset(R.styleable.ItemView_mc_middleTextPadding, 0)
        start = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_middleTextPaddingStart, padding
        )
        top = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_middleTextPaddingTop, padding
        )
        end = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_middleTextPaddingEnd, padding
        )
        bottom = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_middleTextPaddingBottom, padding
        )
        setTextPadding(middleItem, start, top, end, bottom)

        padding = a.getDimensionPixelOffset(R.styleable.ItemView_mc_rightTextPadding, 0)
        start = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_rightTextPaddingStart, padding
        )
        top = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_rightTextPaddingTop, padding
        )
        end = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_rightTextPaddingEnd, padding
        )
        bottom = a.getDimensionPixelOffset(
            R.styleable.ItemView_mc_rightTextPaddingBottom, padding
        )
        setTextPadding(rightItem, start, top, end, bottom)
    }

    private fun setTextPadding(item: View, start: Int, top: Int, end: Int, bottom: Int) = apply {
        item.setPadding(start, top, end, bottom)
    }

    private fun setHintText(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.labelView.hint = a.getString(styleable)
        }
    }

    private fun setTextColor(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextColor(a.getColorStateList(styleable))
        }
    }

    private fun setTextBackground(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextBackground(a.getDrawable(styleable))
        }
    }

    private fun setTextMaxWidth(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextMaxWidth(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setTextMinWidth(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextMinWidth(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setIconPadding(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIconPadding(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setPaddingBottom() {
        if (dividerVisible) {
            setPaddingBottom(max(dividerHeight, paddingBottom))
        } else {
            if (paddingBottom == dividerHeight) {
                setPaddingBottom(0)
            }
        }
    }

    open fun setMiddleItemLayoutGravity(@GravityInt gravity: Int) {
        middleItem.gravity = gravity
    }

    open fun setIconGravity(@GravityInt gravity: Int) = apply {
        leftItem.setIconGravity(gravity)
        middleItem.setIconGravity(gravity)
        rightItem.setIconGravity(gravity)
    }

    open fun setIconTintColor(color: ColorStateList?) = apply {
        leftItem.setIconColor(color)
        middleItem.setIconColor(color)
        rightItem.setIconColor(color)
    }

    open fun setIconWidth(width: Int) = apply {
        leftItem.setIconWidth(width)
        middleItem.setIconWidth(width)
        rightItem.setIconWidth(width)
    }

    open fun setIconHeight(height: Int) = apply {
        leftItem.setIconHeight(height)
        middleItem.setIconHeight(height)
        rightItem.setIconHeight(height)
    }

    open fun setIconPressedEffect(useRipple: Boolean) = apply {
        if (useRipple) {
            setIconRippleColor(iconRippleColor)
        } else {
            setIconPressedColor(iconPressedColor)
        }
    }

    open fun setIconRippleColor(@ColorInt color: Int) = apply {
        leftItem.iconView.background = RippleDrawable(
            ColorStateList.valueOf(color), null,
            GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ColorStateList.valueOf(color))
            }
        )
        middleItem.iconView.background = RippleDrawable(
            ColorStateList.valueOf(color), null,
            GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ColorStateList.valueOf(color))
            }
        )
        rightItem.iconView.background = RippleDrawable(
            ColorStateList.valueOf(color), null,
            GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ColorStateList.valueOf(color))
            }
        )
    }

    open fun setIconPressedColor(@ColorInt pressedColor: Int) = apply {
        setLeftIconDrawable(leftItem.iconView.background, ColorDrawable(pressedColor))
        setMiddleIconDrawable(middleItem.iconView.background, ColorDrawable(pressedColor))
        setRightIconDrawable(rightItem.iconView.background, ColorDrawable(pressedColor))
    }

    open fun setLeftIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?) = apply {
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
    }

    open fun setMiddleIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?) = apply {
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
    }

    open fun setRightIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?) = apply {
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
    }

    open fun setTextGravity(gravity: Int) = apply {
        leftItem.labelView.gravity = gravity
        middleItem.labelView.gravity = gravity
        rightItem.labelView.gravity = gravity
    }

    open fun setTextColor(textColor: ColorStateList?) = apply {
        leftItem.setTextColor(textColor)
        middleItem.setTextColor(textColor)
        rightItem.setTextColor(textColor)
    }

    open fun setTextSize(textSize: Float) = apply {
        leftItem.setTextSize(textSize)
        middleItem.setTextSize(textSize)
        rightItem.setTextSize(textSize)
    }

    open fun setHintTextColor(textColor: Int) = apply {
        leftItem.labelView.setHintTextColor(textColor)
        middleItem.labelView.setHintTextColor(textColor)
        rightItem.labelView.setHintTextColor(textColor)
    }

    open fun setTextMaxWidth(maxWidth: Int) = apply {
        leftItem.setTextMaxWidth(maxWidth)
        middleItem.setTextMaxWidth(maxWidth)
        rightItem.setTextMaxWidth(maxWidth)
    }

    open fun setTextMinWidth(minWidth: Int) = apply {
        leftItem.setTextMinWidth(minWidth)
        middleItem.setTextMinWidth(minWidth)
        rightItem.setTextMinWidth(minWidth)
    }

    open fun setTextEllipsize(ellipsize: TextUtils.TruncateAt) = apply {
        leftItem.setEllipsize(ellipsize)
        middleItem.setEllipsize(ellipsize)
        rightItem.setEllipsize(ellipsize)
    }

    private fun setTextEllipsize(ellipsize: Int) = apply {
        leftItem.setEllipsize(ellipsize)
        middleItem.setEllipsize(ellipsize)
        rightItem.setEllipsize(ellipsize)
    }

    open fun setLeftItemMargin(left: Int, top: Int, bottom: Int) = apply {
        val layoutParams = leftItem.layoutParams as LayoutParams
        layoutParams.marginStart = left
        layoutParams.topMargin = top
        layoutParams.bottomMargin = bottom
        leftItem.layoutParams = layoutParams
    }

    open fun setMiddleItemMargin(start: Int, top: Int, end: Int, bottom: Int) = apply {
        val layoutParams = middleItem.layoutParams as LayoutParams
        layoutParams.marginStart = start
        layoutParams.topMargin = top
        layoutParams.marginEnd = end
        layoutParams.bottomMargin = bottom
        middleItem.layoutParams = layoutParams
    }

    open fun setRightItemMargin(top: Int, end: Int, bottom: Int) = apply {
        val layoutParams = rightItem.layoutParams as LayoutParams
        layoutParams.topMargin = top
        layoutParams.marginEnd = end
        layoutParams.bottomMargin = bottom
        rightItem.layoutParams = layoutParams
    }

    open fun setIconPadding(padding: Int) = apply {
        leftItem.setIconPadding(padding)
        middleItem.setIconPadding(padding)
        rightItem.setIconPadding(padding)
    }

    open fun setDividerVisible(visible: Boolean) = apply {
        dividerVisible = visible
        setPaddingBottom()
        invalidate()
    }

    open fun setDividerColor(color: Int) = apply {
        dividerColor = color
        invalidate()
    }

    open fun setDividerMarginColor(color: Int) = apply {
        dividerMarginColor = color
        invalidate()
    }

    open fun setDividerHeight(height: Int) = apply {
        dividerHeight = height
        setPaddingBottom()
        invalidate()
    }

    open fun setDividerMargin(marginStart: Int, marginEnd: Int) = apply {
        dividerMarginStart = marginStart
        dividerMarginEnd = marginEnd
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasure = heightMeasureSpec
        val mode = MeasureSpec.getMode(heightMeasure)
        if (mode != MeasureSpec.EXACTLY && excludePaddingHeight > 0) {
            val height = excludePaddingHeight + paddingTop + paddingBottom
            heightMeasure = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasure)
    }
}