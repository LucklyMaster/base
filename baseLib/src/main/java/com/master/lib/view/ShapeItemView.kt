package com.master.lib.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import com.master.lib.R
import com.master.lib.ext.ifHas

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
        setTextInputType(a)
        setTextDigits(a)
        a.recycle()
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

    fun setLeftEditable(editable: Boolean) = apply {
        leftItem.setEditable(editable)
    }

    fun setMiddleEditable(editable: Boolean) = apply {
        middleItem.setEditable(editable)
    }

    fun setRightEditable(editable: Boolean) = apply {
        rightItem.setEditable(editable)
    }
}