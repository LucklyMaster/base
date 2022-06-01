package com.masterchan.lib.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * @author MasterChan
 * @date 2021-12-29 14:11
 * @describe BitmapUtils
 */
object BitmapUtils {

    /**
     * 从本地路径中获取Bitmap，如果图片超过[maxWidth]/[maxHeight]会按照
     * [BitmapFactory.Options.inSampleSize]进行宽高缩放
     * @param path 文件路径
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @return Bitmap
     */
    fun getBitmap(
        path: String, maxWidth: Int = -1, maxHeight: Int = -1,
        inPreferredConfig: Bitmap.Config = Bitmap.Config.ARGB_8888
    ): Bitmap? {
        if (maxWidth <= 0 && maxHeight <= 0) {
            return try {
                BitmapFactory.decodeFile(path)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                null
            }
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        var width = maxWidth
        var height = maxHeight
        width = if (width <= 0) options.outWidth else width
        height = if (height <= 0) options.outHeight else height
        options.inPreferredConfig = inPreferredConfig
        options.inSampleSize = calculateInSampleSize(options, width, height)
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
    private fun calculateInSampleSize(
        options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (width > maxWidth || height > maxHeight) {
            val heightRatio = (height.toFloat() / maxHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / maxWidth.toFloat()).roundToInt()
            inSampleSize = max(widthRatio, heightRatio)
        }
        return inSampleSize
    }
}