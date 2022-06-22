package com.masterchan.lib.utils

import android.graphics.*
import android.media.ExifInterface
import android.util.Size
import com.masterchan.lib.ext.screenHeight
import com.masterchan.lib.ext.screenWidth
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * ImageUtils
 * @author: MasterChan
 * @date: 2022-5-24 21:34
 */
object ImageUtils {

    /**
     * 获取图片旋转角度
     * @param path 图片路径
     * @return 0,90,180,270
     */
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

    fun getBitmap(
        path: String,
        inPreferredConfig: Bitmap.Config = Bitmap.Config.ARGB_8888
    ): Bitmap? {
        val maxBitmapSize = sqrt(
            screenWidth.toDouble().pow(2) + screenHeight.toDouble().pow(2)
        ).toInt()
        return getBitmap(path, maxBitmapSize, maxBitmapSize, inPreferredConfig)
    }

    /**
     * 从本地路径中获取Bitmap，如果图片超过[maxWidth]/[maxHeight]会按照
     * [BitmapFactory.Options.inSampleSize]进行宽高缩放
     * @param path 文件路径
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @return Bitmap?
     */
    fun getBitmap(
        path: String,
        maxWidth: Int,
        maxHeight: Int,
        inPreferredConfig: Bitmap.Config = Bitmap.Config.ARGB_8888
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        options.inPreferredConfig = inPreferredConfig
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return try {
            BitmapFactory.decodeFile(path, options)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 计算Bitmap的缩放比例
     * @param options Options
     * @param maxWidth Int
     * @param maxHeight Int
     * @return Int
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (width > maxWidth || height > maxHeight) {
            //也可以通过Bitmap占用的内存和当前应用剩余内存比较，计算最大宽高
            while (width / inSampleSize > maxWidth || height / inSampleSize > maxHeight) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * 获取图片宽高
     * @param path 图片路径
     * @return Size
     */
    fun getImageSize(path: String): Size {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        return Size(options.outWidth, options.outHeight)
    }
}