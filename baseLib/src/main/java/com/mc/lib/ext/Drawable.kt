package com.mc.lib.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable

fun Drawable.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
): ByteArray {
    return toBitmap().toByteArray(format, quality)
}

fun ByteArray.toDrawable(options: BitmapFactory.Options? = null): Drawable {
    return toBitmap(options).toDrawable(application.resources)
}