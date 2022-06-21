package com.masterchan.lib.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.masterchan.lib.ext.screenHeight
import com.masterchan.lib.ext.screenWidth
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * BitmapUtils
 * @author MasterChan
 * @date 2021-12-29 14:11
 */
object BitmapUtils {

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
}