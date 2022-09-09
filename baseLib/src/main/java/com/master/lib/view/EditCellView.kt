package com.master.lib.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText
import android.widget.TextView
import com.master.lib.R
import com.master.lib.ext.ifHas
import com.master.lib.ext.isEditable
import com.master.lib.ext.setDigits

/**
 * 与[CellView]一样使用，[labelView]继承自[EditText]
 * @author: MasterChan
 * @date: 2022-07-13 00:00
 */
open class EditCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : CellView(context, attrs, defStyleAttr, defStyleRes) {

    open val isEditable: Boolean
        get() = (labelView as EditText).isEditable

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

    override fun createLabelView(): TextView {
        return EditText(context)
    }

    open fun setEditable(editable: Boolean) = apply {
        (labelView as EditText).isEditable = editable
    }

    open fun setInputType(type: Int) = apply {
        labelView.inputType = type
    }

    open fun setDigits(digits: CharSequence) {
        labelView.setDigits(digits.toString())
    }
}