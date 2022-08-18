package com.master.lib.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.master.lib.utils.ColorFilterUtils

fun getDrawable(@DrawableRes res: Int): Drawable? {
    return ContextCompat.getDrawable(application, res)
}

fun Drawable.toByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
): ByteArray {
    return toBitmap().toByteArray(format, quality)
}

fun ByteArray.toDrawable(options: BitmapFactory.Options? = null): Drawable {
    return toBitmap(options).toDrawable(application.resources)
}

fun Drawable.toDark(alpha: Float): Drawable {
    colorFilter = ColorFilterUtils.getDarkColorFilter(alpha)
    return this
}