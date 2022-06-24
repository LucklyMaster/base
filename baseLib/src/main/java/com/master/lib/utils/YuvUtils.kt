package com.master.lib.utils

import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream

/**
 * YuvUtils
 * @author: MasterChan
 * @date: 2022-6-22 22:45
 */
object YuvUtils {
    /**
     * 将yuv图片旋转90°，旋转后图片的宽高将会置换
     * @param data yuv图片
     * @param width 图片宽度
     * @param height 图片高度
     * @return 旋转后的图片
     */
    fun rotateYUV420Degree90(data: ByteArray, width: Int, height: Int): ByteArray {
        val yuv = ByteArray(width * height * 3 / 2)
        var i = 0
        for (x in 0 until width) {
            for (y in height - 1 downTo 0) {
                yuv[i] = data[y * width + x]
                i++
            }
        }
        i = width * height * 3 / 2 - 1
        var x = width - 1
        while (x > 0) {
            for (y in 0 until height / 2) {
                yuv[i] = data[width * height + y * width + x]
                i--
                yuv[i] = data[width * height + y * width + (x - 1)]
                i--
            }
            x -= 2
        }
        return yuv
    }

    /**
     * 将yuv图片旋转180°
     * @param data yuv图片
     * @param width 图片宽度
     * @param height 图片高度
     * @return 旋转后的图片
     */
    fun rotateYUV420Degree180(data: ByteArray, width: Int, height: Int): ByteArray {
        val yuv = ByteArray(width * height * 3 / 2)
        var count = 0
        var i = width * height - 1
        while (i >= 0) {
            yuv[count] = data[i]
            count++
            i--
        }
        i = width * height * 3 / 2 - 1
        while (i >= width * height) {
            yuv[count++] = data[i - 1]
            yuv[count++] = data[i]
            i -= 2
        }
        return yuv
    }

    /**
     * 将yuv图片旋转270°，旋转后图片的宽高将会置换
     * @param data yuv图片
     * @param width 图片宽度
     * @param height 图片高度
     * @return 旋转后的图片
     */
    fun rotateYUV420Degree270(data: ByteArray, width: Int, height: Int): ByteArray {
        val yuv = ByteArray(width * height * 3 / 2)
        var i = 0
        for (x in width - 1 downTo 0) {
            for (y in 0 until height) {
                yuv[i] = data[y * width + x]
                i++
            }
        }
        i = width * height
        var x = width - 1
        while (x > 0) {
            for (y in 0 until height / 2) {
                yuv[i] = data[width * height + y * width + (x - 1)]
                i++
                yuv[i] = data[width * height + y * width + x]
                i++
            }
            x -= 2
        }
        return yuv
    }

    /**
     * 将yuv图片旋转270°并且做镜像，旋转后图片的宽高将会置换
     * @param data yuv图片
     * @param width 图片宽度
     * @param height 图片高度
     * @return 旋转后的图片
     */
    fun rotateYUVDegree270AndMirror(data: ByteArray, width: Int, height: Int): ByteArray {
        val yuv = ByteArray(width * height * 3 / 2)
        var i = 0
        var maxY: Int
        for (x in width - 1 downTo 0) {
            maxY = width * (height - 1) + x * 2
            for (y in 0 until height) {
                yuv[i] = data[maxY - (y * width + x)]
                i++
            }
        }
        val uvSize = width * height
        i = uvSize
        var maxUV: Int
        var x = width - 1
        while (x > 0) {
            maxUV = width * (height / 2 - 1) + x * 2 + uvSize
            for (y in 0 until height / 2) {
                yuv[i] = data[maxUV - 2 - (y * width + x - 1)]
                i++
                yuv[i] = data[maxUV - (y * width + x)]
                i++
            }
            x -= 2
        }
        return yuv
    }

    /**
     * nv21数据转为rgb图片
     * @param data nv21
     * @param width  nv21宽度
     * @param height nv21高度
     * @return rgb图片
     */
    fun yuv2Rgb(data: ByteArray, width: Int, height: Int): ByteArray {
        val image = YuvImage(data, ImageFormat.NV21, width, height, null)
        val stream = ByteArrayOutputStream()
        image.compressToJpeg(Rect(0, 0, width, height), 100, stream)
        return stream.toByteArray()
    }

    /**
     * RGB数据转化为NV21数据
     * @param rgb argb数据
     * @param width  宽度
     * @param height 高度
     * @return nv21数据
     */
    fun rgb2Yuv(rgb: IntArray, width: Int, height: Int): ByteArray {
        val frameSize = width * height
        var yIndex = 0
        var uvIndex = frameSize
        var index = 0
        val nv21 = ByteArray(width * height * 3 / 2)
        for (j in 0 until height) {
            for (i in 0 until width) {
                val r = rgb[index] and 0xFF0000 shr 16
                val g = rgb[index] and 0x00FF00 shr 8
                val b = rgb[index] and 0x0000FF
                val y = (66 * r + 129 * g + 25 * b + 128 shr 8) + 16
                val u = (-38 * r - 74 * g + 112 * b + 128 shr 8) + 128
                val v = (112 * r - 94 * g - 18 * b + 128 shr 8) + 128
                nv21[yIndex++] = (if (y < 0) 0 else if (y > 255) 255 else y).toByte()
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.size - 2) {
                    nv21[uvIndex++] = (if (v < 0) 0 else if (v > 255) 255 else v).toByte()
                    nv21[uvIndex++] = (if (u < 0) 0 else if (u > 255) 255 else u).toByte()
                }
                ++index
            }
        }
        return nv21
    }
}