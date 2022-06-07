package com.masterchan.lib.ext

import android.graphics.drawable.Drawable
import android.view.Gravity
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