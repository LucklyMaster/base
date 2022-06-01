package com.masterchan.lib.utils

import android.media.ExifInterface
import java.io.IOException

/**
 * @author: MasterChan
 * @date: 2022-5-24 21:34
 * @describe: ImageUtils
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
}