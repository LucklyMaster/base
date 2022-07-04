package com.master.lib.ext

import android.content.res.TypedArray
import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes

fun TypedArray.getColorInt(@StyleableRes index: Int, @ColorRes colorRes: Int): Int {
    return getColor(index, application.getColor(colorRes))
}

fun TypedArray.getColor(@StyleableRes index: Int, colorValue: String): Int {
    return getColor(index, Color.parseColor(colorValue))
}