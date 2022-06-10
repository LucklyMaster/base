package com.masterchan.lib.utils

import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.util.Size
import java.io.IOException

/**
 * ImageUtils
 * @author: MasterChan
 * @date: 2022-5-24 21:34
 */
object ImageUtils {

    fun getImageDegree(path: String?): Int {
        try {
            val exifInterface = ExifInterface(path!!)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 获取图片宽高
     * @param path String
     * @return Size
     */
    fun getImageSize(path: String): Size {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        return Size(options.outWidth, options.outHeight)
    }
}