package com.master.lib.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import com.master.lib.ext.ifHas
import com.master.lib.ext.isEditable
import com.masterchan.lib.R

/**
 * ShapeItemView
 * @author MasterChan
 * @date 2021-12-13 15:00
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_shapeItemViewDefaultStyle,
    defStyleRes: Int = R.style.mc_ShapeItemView
) : ItemView(context, attrs, defStyleAttr, defStyleRes), IShapeView {

    private var leftText: EditCellView? = null
    private var middleText: EditCellView? = null
    private var rightText: EditCellView? = null

    override val leftItem: EditCellView
        get() {
            if (leftText == null) {
                leftText = EditCellView(context)
            }
            return leftText!!
        }
    override val middleItem: EditCellView
        get() {
            if (middleText == null) {
                middleText = EditCellView(context)
            }
            return middleText!!
        }
    override val rightItem: EditCellView
        get() {
            if (rightText == null) {
                rightText = EditCellView(context)
            }
            return rightText!!
        }
    override val shapeHelper: ShapeViewHelper by lazy { ShapeViewHelper(this, attrs) }

    init {
        shapeHelper.into()

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ShapeItemView, defStyleAttr, defStyleRes
        )
        setLeftEditable(a.getBoolean(R.styleable.ShapeItemView_mc_leftEditable, false))
        setMiddleEditable(a.getBoolean(R.styleable.ShapeItemView_mc_middleEditable, false))
        setRightEditable(a.getBoolean(R.styleable.ShapeItemView_mc_rightEditable, false))
        setTextPadding(a)
        setTextInputType(a)
        setTextDigits(a)
        a.recycle()
    }

    private fun setTextPadding(a: TypedArray) {
        var start: Int
        var top: Int
        var end: Int
        var bottom: Int
        var padding = a.getDimensionPixelOffset(R.styleable.ShapeItemView_mc_leftTextPadding, 0)
        start = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_leftTextPaddingStart, padding
        )
        top = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_leftTextPaddingTop, padding
        )
        end = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_leftTextPaddingEnd, padding
        )
        bottom = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_leftTextPaddingBottom, padding
        )
        setTextPadding(leftItem, start, top, end, bottom)

        padding = a.getDimensionPixelOffset(R.styleable.ShapeItemView_mc_middleTextPadding, 0)
        start = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_middleTextPaddingStart, padding
        )
        top = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_middleTextPaddingTop, padding
        )
        end = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_middleTextPaddingEnd, padding
        )
        bottom = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_middleTextPaddingBottom, padding
        )
        setTextPadding(middleItem, start, top, end, bottom)

        padding = a.getDimensionPixelOffset(R.styleable.ShapeItemView_mc_rightTextPadding, 0)
        start = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_rightTextPaddingStart, padding
        )
        top = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_rightTextPaddingTop, padding
        )
        end = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_rightTextPaddingEnd, padding
        )
        bottom = a.getDimensionPixelOffset(
            R.styleable.ShapeItemView_mc_rightTextPaddingBottom, padding
        )
        setTextPadding(rightItem, start, top, end, bottom)
    }

    private fun setTextInputType(a: TypedArray) {
        a.ifHas(R.styleable.ShapeItemView_mc_leftInputType) {
            leftItem.setInputType(a.getInteger(it, 0))
        }
        a.ifHas(R.styleable.ShapeItemView_mc_middleInputType) {
            leftItem.setInputType(a.getInteger(it, 0))
        }
        a.ifHas(R.styleable.ShapeItemView_mc_rightInputType) {
            leftItem.setInputType(a.getInteger(it, 0))
        }
    }

    private fun setTextDigits(a: TypedArray) {
        a.ifHas(R.styleable.ShapeItemView_mc_leftDigits) { leftItem.setDigits(a.getText(it)) }
        a.ifHas(R.styleable.ShapeItemView_mc_middleDigits) { leftItem.setDigits(a.getText(it)) }
        a.ifHas(R.styleable.ShapeItemView_mc_rightDigits) { leftItem.setDigits(a.getText(it)) }
    }

    private fun setTextPadding(item: View, start: Int, top: Int, end: Int, bottom: Int) = apply {
        item.setPadding(start, top, end, bottom)
    }

    fun setLeftEditable(editable: Boolean) = apply {
        leftItem.labelView.isEditable = editable
    }

    fun setMiddleEditable(editable: Boolean) = apply {
        middleItem.labelView.isEditable = editable
    }

    fun setRightEditable(editable: Boolean) = apply {
        rightItem.labelView.isEditable = editable
    }
}