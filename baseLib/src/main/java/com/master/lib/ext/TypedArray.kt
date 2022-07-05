package com.master.lib.ext

import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.StyleableRes

fun TypedArray.getColor(@StyleableRes index: Int, colorValue: String): Int {
    return getColor(index, Color.parseColor(colorValue))
}