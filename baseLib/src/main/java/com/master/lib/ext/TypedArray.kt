package com.master.lib.ext

import android.content.res.TypedArray
import android.graphics.Color
import androidx.annotation.StyleableRes

fun TypedArray.getColor(@StyleableRes index: Int, colorValue: String): Int {
    return getColor(index, Color.parseColor(colorValue))
}

inline fun TypedArray.ifHas(@StyleableRes index: Int, method: (Int) -> Unit) {
    if (hasValue(index)) {
        method.invoke(index)
    }
}