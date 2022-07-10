package com.master.lib.ext

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView

fun TextView.setDrawable(drawable: Drawable?, gravity: Int) {
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    when (gravity) {
        Gravity.START -> setCompoundDrawables(drawable, null, null, null)
        Gravity.TOP -> setCompoundDrawables(null, drawable, null, null)
        Gravity.END -> setCompoundDrawables(null, null, drawable, null)
        Gravity.BOTTOM -> setCompoundDrawables(null, null, null, drawable)
    }
}

var EditText.isEditable: Boolean
    get() {
        return isFocusable && isLongClickable && isFocusableInTouchMode
    }
    set(value) {
        isFocusable = value
        isLongClickable = value
        isFocusableInTouchMode = value
        if (!value) {
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                true
            }
        } else {
            setOnTouchListener(null)
        }
    }