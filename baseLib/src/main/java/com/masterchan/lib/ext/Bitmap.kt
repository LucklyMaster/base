package com.masterchan.lib.ext

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(format: CompressFormat = CompressFormat.PNG, quality: Int = 100): ByteArray {
    val bos = ByteArrayOutputStream()
    compress(format, quality, bos)
    return bos.toByteArray()
}

fun ByteArray.toBitmap(options: BitmapFactory.Options? = null): Bitmap {
    return if (options == null) {
        BitmapFactory.decodeByteArray(this, 0, size)
    } else {
        BitmapFactory.decodeByteArray(this, 0, size, options)
    }
}

fun Bitmap.scale(width: Int, height: Int) {
    Bitmap.createScaledBitmap(this, width, height, true)
}

