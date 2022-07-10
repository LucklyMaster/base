package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import com.master.lib.ext.isEditable
import com.masterchan.lib.R

/**
 * EditCellView
 * @author: MasterChan
 * @date: 2022-07-10 22:19
 */
class EditCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : CellView(context, attrs, defStyleAttr, defStyleRes) {

    override val labelView = EditText(context)

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.EditCellView, defStyleAttr, defStyleRes
        )
        labelView.isEditable = a.getBoolean(R.styleable.EditCellView_mc_editable, false)
        a.recycle()
    }
}