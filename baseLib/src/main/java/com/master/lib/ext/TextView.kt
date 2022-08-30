package com.master.lib.ext

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.method.DigitsKeyListener
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes

fun TextView.setText(@StringRes res: Int, vararg format: Any) {
    text = resources.getString(res, format)
}

fun TextView.setDrawable(drawable: Drawable?, gravity: Int) {
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    when (gravity) {
        Gravity.START -> setCompoundDrawables(drawable, null, null, null)
        Gravity.TOP -> setCompoundDrawables(null, drawable, null, null)
        Gravity.END -> setCompoundDrawables(null, null, drawable, null)
        Gravity.BOTTOM -> setCompoundDrawables(null, null, null, drawable)
    }
}

fun TextView.setDigits(digits: String) {
    keyListener = DigitsKeyListener.getInstance(digits)
}

var EditText.isEditable: Boolean
    get() {
        return isFocusable && isLongClickable && isFocusableInTouchMode
    }
    @SuppressLint("ClickableViewAccessibility")
    set(value) {
        isFocusable = value
        isLongClickable = value
        isFocusableInTouchMode = value
        if (!value) {
            val gestureDetector = GestureDetector(context, object :
                GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    performClick()
                    return true
                }
            })
            setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        } else {
            setOnTouchListener(null)
        }
    }