package com.masterchan.lib.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.masterchan.lib.ext.application
import com.masterchan.lib.ext.topActivity
import com.masterchan.lib.log.MLog
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * BitmapUtils
 * @author MasterChan
 * @date 2021-12-29 14:11
 */
object BitmapUtils {

    private var maxBitmapSize = 0

    fun getBitmap(
        path: String,
        inPreferredConfig: Bitmap.Config = Bitmap.Config.RGB_565
    ): Bitmap? {
        if (maxBitmapSize == 0) {
            maxBitmapSize = getMaxBitmapSize()
        }
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
            while (width / inSampleSize > maxWidth || height / inSampleSize > maxHeight) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun getMaxBitmapSize(): Int {
        val size = Point()
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            topActivity?.display
        } else {
            (application.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        }
        display?.getSize(size)
        val width = size.x
        val height = size.y

        var maxBitmapSize = sqrt(width.toDouble().pow(2.0) + height.toDouble().pow(2.0)).toInt()

        // Check for max texture size via Canvas
        /*val canvas = Canvas()
        val maxCanvasSize = canvas.maximumBitmapWidth.coerceAtMost(canvas.maximumBitmapHeight)
        if (maxCanvasSize > 0) {
            maxBitmapSize = max(maxCanvasSize, maxBitmapSize)
            // maxBitmapSize = maxBitmapSize.coerceAtMost(maxCanvasSize)
        }*/

        // Check for max texture size via GL
        val maxTextureSize: Int = EglUtils.maxTextureSize
        if (maxTextureSize > 0) {
            maxBitmapSize = max(maxBitmapSize, maxTextureSize)
            // maxBitmapSize = maxBitmapSize.coerceAtMost(maxTextureSize)
        }
        MLog.d("maxBitmapSize = $maxBitmapSize")
        return maxBitmapSize
    }
}