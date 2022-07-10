package com.master.lib.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.master.lib.ext.activity
import com.master.lib.ext.dp2pxi
import com.master.lib.ext.getColor
import com.master.lib.ext.setPaddingBottom
import com.masterchan.lib.R
import kotlin.math.max

/**
 * TitleBar
 * @author MasterChan
 * @date 2021-12-09 18:37
 */
@Suppress("MemberVisibilityCanBePrivate")
open class TitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_titleBarDefaultStyle,
    defStyleRes: Int = R.style.mc_TitleBar
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    val leftItem = CellView(context)
    val middleItem = CellView(context)
    val rightItem = CellView(context)
    private var iconRippleColor = 0
    private var iconPressedColor = 0
    private var middleItemLayoutGravity = 1

    /**
     * 除去Padding之后的高度，适用于[fitSystemWindows]时，系统为View添加Padding后，如果View
     * 是固定高度，会被压缩场景；
     * 此字段只有在[android:layout_height="wrap_content"]时生效，使用此字段后，View的实际
     * 高度为[excludePaddingHeight]+[getPaddingBottom]+[getPaddingTop]
     */
    private var excludePaddingHeight = 0

    private var dividerVisible = false
    private var dividerHeight = 0
    private var dividerColor: Int = 0
    private var dividerMarginStart = 0
    private var dividerMarginEnd = 0
    private var dividerMarginColor = 0

    private val dividerPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

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
        val useIconTint = a.getBoolean(R.styleable.TitleBar_mc_useIconTint, true)
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
        iconRippleColor = a.getColor(R.styleable.TitleBar_mc_iconRippleColor, 0)
        iconPressedColor = a.getColor(R.styleable.TitleBar_mc_iconPressedColor, 0)
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
            setTextSize(a.getDimension(R.styleable.TitleBar_mc_textSize, 0f))
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
        dividerVisible = a.getBoolean(R.styleable.TitleBar_mc_dividerVisible, false)
        dividerColor = a.getColor(R.styleable.TitleBar_mc_dividerColor, "#D6D6D6")
        dividerMarginColor = a.getColor(R.styleable.TitleBar_mc_dividerMarginColor, "#00000000")
        dividerHeight = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_dividerHeight, 0)
        dividerMarginStart = a.getDimensionPixelOffset(
            R.styleable.TitleBar_mc_dividerMarginStart, 0
        )
        dividerMarginEnd = a.getDimensionPixelOffset(R.styleable.TitleBar_mc_dividerMarginEnd, 0)
        setPaddingBottom()

        excludePaddingHeight = a.getDimensionPixelOffset(
            R.styleable.TitleBar_mc_excludePaddingHeight, context.dp2pxi(50f)
        )
        setClickLeftFinish(a.getBoolean(R.styleable.TitleBar_mc_clickLeftFinish, true))
        a.recycle()
    }

    private fun layout() {
        leftItem.id = Int.MAX_VALUE - 1001
        middleItem.id = Int.MAX_VALUE - 1002
        rightItem.id = Int.MAX_VALUE - 1003

        addView(leftItem, LayoutParams(LayoutParams.WRAP_CONTENT, 0))
        addView(middleItem, LayoutParams(LayoutParams.WRAP_CONTENT, 0))
        addView(rightItem, LayoutParams(LayoutParams.WRAP_CONTENT, 0))

        val sets = ConstraintSet()
        val parentId = ConstraintSet.PARENT_ID
        sets.clone(this)

        sets.connect(leftItem.id, ConstraintSet.START, parentId, ConstraintSet.START)
        sets.connect(leftItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        sets.connect(leftItem.id, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM)

        sets.connect(middleItem.id, ConstraintSet.START, parentId, ConstraintSet.START)
        sets.connect(middleItem.id, ConstraintSet.TOP, parentId, ConstraintSet.TOP)
        sets.connect(middleItem.id, ConstraintSet.END, parentId, ConstraintSet.END)
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

    /**
     * 设置[middleItem]的布局方式，一共三个取值
     * 0:Start,1:Center,2:End
     * @param gravity Int
     */
    fun setMiddleItemLayoutGravity(@IntRange(from = 0, to = 2) gravity: Int) {
        if (gravity == middleItemLayoutGravity) {
            return
        }
        middleItemLayoutGravity = gravity
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

    private fun setIconGravity(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIconGravity(getIconGravity(a.getInt(styleable, 0)))
        }
    }

    /**
     * 将自定义的Gravity转换为系统Gravity
     * @param value Int
     * @return Int
     */
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
            item.setIconWidth(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setIconHeight(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setIconHeight(a.getDimensionPixelOffset(styleable, 0))
        }
    }

    private fun setText(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setText(a.getString(styleable)!!)
        }
    }

    private fun setTextGravity(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.labelView.gravity = getTextGravity(a.getInt(styleable, 0))
        }
    }

    private fun setTextSize(a: TypedArray, styleable: Int, item: CellView) {
        if (a.hasValue(styleable)) {
            item.setTextSize(a.getDimension(styleable, 0f))
        }
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

    /**
     * 设置所有item图标的gravity，一共4个取值
     * 0:start,1:top,2:end,3:bottom
     * @param gravity 0,1,2,3
     * @return TitleBar
     */
    fun setIconGravity(@IntRange(from = 0, to = 3) gravity: Int) = apply {
        val gravityValue = getIconGravity(gravity)
        leftItem.setIconGravity(gravityValue)
        middleItem.setIconGravity(gravityValue)
        rightItem.setIconGravity(gravityValue)
    }

    fun setIconTintColor(color: ColorStateList?) = apply {
        leftItem.setIconColor(color)
        middleItem.setIconColor(color)
        rightItem.setIconColor(color)
    }

    fun setIconWidth(width: Int) = apply {
        leftItem.setIconWidth(width)
        middleItem.setIconWidth(width)
        rightItem.setIconWidth(width)
    }

    fun setIconHeight(height: Int) = apply {
        leftItem.setIconHeight(height)
        middleItem.setIconHeight(height)
        rightItem.setIconHeight(height)
    }

    fun setIconPressedEffect(useRipple: Boolean) = apply {
        if (useRipple) {
            setIconRippleColor(iconRippleColor)
        } else {
            setIconPressedColor(iconPressedColor)
        }
    }

    fun setIconRippleColor(@ColorInt color: Int) = apply {
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

    fun setIconPressedColor(@ColorInt pressedColor: Int) = apply {
        setLeftIconDrawable(leftItem.iconView.background, ColorDrawable(pressedColor))
        setMiddleIconDrawable(middleItem.iconView.background, ColorDrawable(pressedColor))
        setRightIconDrawable(rightItem.iconView.background, ColorDrawable(pressedColor))
    }

    fun setLeftIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?) = apply {
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

    fun setMiddleIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?) = apply {
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

    fun setRightIconDrawable(normalDrawable: Drawable?, pressedDrawable: Drawable?) = apply {
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

    fun setTextGravity(gravity: Int) = apply {
        val gravityValue = getTextGravity(gravity)
        leftItem.labelView.gravity = gravityValue
        middleItem.labelView.gravity = gravityValue
        rightItem.labelView.gravity = gravityValue
    }

    fun setTextColor(textColor: ColorStateList?) = apply {
        leftItem.setTextColor(textColor)
        middleItem.setTextColor(textColor)
        rightItem.setTextColor(textColor)
    }

    fun setTextSize(textSize: Float) = apply {
        leftItem.setTextSize(textSize)
        middleItem.setTextSize(textSize)
        rightItem.setTextSize(textSize)
    }

    fun setHintTextColor(textColor: Int) = apply {
        leftItem.labelView.setHintTextColor(textColor)
        middleItem.labelView.setHintTextColor(textColor)
        rightItem.labelView.setHintTextColor(textColor)
    }

    fun setTextMaxWidth(maxWidth: Int) = apply {
        leftItem.setTextMaxWidth(maxWidth)
        middleItem.setTextMaxWidth(maxWidth)
        rightItem.setTextMaxWidth(maxWidth)
    }

    fun setTextMinWidth(minWidth: Int) = apply {
        leftItem.setTextMinWidth(minWidth)
        middleItem.setTextMinWidth(minWidth)
        rightItem.setTextMinWidth(minWidth)
    }

    fun setTextEllipsize(ellipsize: TextUtils.TruncateAt) = apply {
        leftItem.setEllipsize(ellipsize)
        middleItem.setEllipsize(ellipsize)
        rightItem.setEllipsize(ellipsize)
    }

    private fun setTextEllipsize(ellipsize: Int) = apply {
        leftItem.setEllipsize(ellipsize)
        middleItem.setEllipsize(ellipsize)
        rightItem.setEllipsize(ellipsize)
    }

    fun setLeftItemMargin(left: Int, top: Int, bottom: Int) = apply {
        val layoutParams = leftItem.layoutParams as LayoutParams
        layoutParams.marginStart = left
        layoutParams.topMargin = top
        layoutParams.bottomMargin = bottom
        leftItem.layoutParams = layoutParams
    }

    fun setMiddleItemMargin(start: Int, top: Int, end: Int, bottom: Int) = apply {
        val layoutParams = middleItem.layoutParams as LayoutParams
        layoutParams.marginStart = start
        layoutParams.topMargin = top
        layoutParams.marginEnd = end
        layoutParams.bottomMargin = bottom
        middleItem.layoutParams = layoutParams
    }

    fun setRightItemMargin(top: Int, end: Int, bottom: Int) = apply {
        val layoutParams = rightItem.layoutParams as LayoutParams
        layoutParams.topMargin = top
        layoutParams.marginEnd = end
        layoutParams.bottomMargin = bottom
        rightItem.layoutParams = layoutParams
    }

    fun setIconPadding(padding: Int) = apply {
        leftItem.setIconPadding(padding)
        middleItem.setIconPadding(padding)
        rightItem.setIconPadding(padding)
    }

    fun setDividerVisible(visible: Boolean) = apply {
        dividerVisible = visible
        setPaddingBottom()
        invalidate()
    }

    fun setDividerColor(color: Int) = apply {
        dividerColor = color
        invalidate()
    }

    fun setDividerMarginColor(color: Int) = apply {
        dividerMarginColor = color
        invalidate()
    }

    fun setDividerHeight(height: Int) = apply {
        dividerHeight = height
        setPaddingBottom()
        invalidate()
    }

    fun setDividerMargin(marginStart: Int, marginEnd: Int) = apply {
        dividerMarginStart = marginStart
        dividerMarginEnd = marginEnd
        invalidate()
    }

    fun setClickLeftFinish(finish: Boolean) = apply {
        if (!finish) {
            leftItem.iconView.setOnClickListener(null)
            leftItem.labelView.setOnClickListener(null)

        }
        if (leftItem.iconDrawable != null) {
            leftItem.iconView.setOnClickListener { activity?.finish() }

        }
        if (leftItem.text.isNotEmpty()) {
            leftItem.labelView.setOnClickListener { activity?.finish() }
        }
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