package com.master.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText
import com.master.R
import com.master.ext.ifHas
import com.master.ext.isEditable
import com.master.ext.setDigits

/**
 * 与[CellView]一样使用，[labelView]继承自[EditText]
 * @author: MasterChan
 * @date: 2022-07-13 00:00
 */
class EditCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : CellView(context, attrs, defStyleAttr, defStyleRes) {

    private var editText: EditText? = null

    override val labelView: EditText
        get() {
            if (editText == null) {
                editText = EditText(context)
            }
            return editText!!
        }

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.EditCellView, defStyleAttr, defStyleRes
        )
        setEditable(a.getBoolean(R.styleable.EditCellView_mc_editable, true))
        setInputType(
            a.getInteger(R.styleable.EditCellView_android_inputType, InputType.TYPE_CLASS_TEXT)
        )
        a.ifHas(R.styleable.EditCellView_android_digits) { setDigits(a.getText(it)) }
        a.recycle()
    }

    fun setEditable(editable: Boolean) = apply {
        labelView.isEditable = editable
    }

    fun setInputType(type: Int) = apply {
        labelView.inputType = type
    }

    fun setDigits(digits: CharSequence) {
        labelView.setDigits(digits.toString())
    }

    val isEditable: Boolean
        get() = labelView.isEditable
}