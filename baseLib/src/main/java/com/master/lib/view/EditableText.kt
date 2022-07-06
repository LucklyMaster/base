package com.master.lib.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.masterchan.lib.R

/**
 * EditableText
 * @author MasterChan
 * @date 2021-12-28 15:46
 */
@Suppress("MemberVisibilityCanBePrivate")
class EditableText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int =  androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var drawable: Drawable? = null
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
        drawable = background
        val a = context.obtainStyledAttributes(attrs, R.styleable.EditableText, defStyleAttr, 0)
        setEditable(a.getBoolean(R.styleable.EditableText_mc_editable, false))
        a.recycle()
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
        background = if (editable) drawable else null
        // if (editable) {
        //     val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.editTextBackground))
        //     background = a.getDrawable(0)
        //     a.recycle()
        // } else {
        //     background = null
        // }
    }
}