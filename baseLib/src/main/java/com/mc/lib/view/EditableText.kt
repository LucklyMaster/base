package com.mc.lib.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText

/**
 * EditableText
 * @author MasterChan
 * @date 2021-12-28 15:46
 */
@Suppress("MemberVisibilityCanBePrivate")
class EditableText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var editable: Boolean = true
        set(value) {
            isCursorVisible = value
            setEditable(value)
            field = value
        }

    constructor(context: Context, editable: Boolean) : this(context) {
        this.editable = editable
    }

    init {
        background = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event) && editable
    }

    @JvmName("setEditable1")
    private fun setEditable(editable: Boolean) {
        isFocusable = editable
        isLongClickable = editable
        isFocusableInTouchMode = editable
    }
}