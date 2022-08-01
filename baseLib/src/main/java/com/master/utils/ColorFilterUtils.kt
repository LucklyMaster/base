package com.master.utils

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

/**
 * 颜色过滤工具
 * @author: MasterChan
 * @date: 2022-06-25 21:25
 */
object ColorFilterUtils {

    /**
     * 获取暗色的ColorFilter
     * @param darkAlpha 透明度
     * @return ColorMatrixColorFilter
     */
    fun getDarkColorFilter(darkAlpha: Float): ColorMatrixColorFilter {
        return ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    darkAlpha, 0f, 0f, 0f, 0f,
                    0f, darkAlpha, 0f, 0f, 0f,
                    0f, 0f, darkAlpha, 0f, 0f,
                    0f, 0f, 0f, 2f, 0f
                )
            )
        )
    }
}