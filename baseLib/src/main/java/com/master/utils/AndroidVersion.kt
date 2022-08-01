package com.master.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast


/**
 * AndroidVersion
 * @author: MasterChan
 * @date: 2022-07-25 10:18
 */
object AndroidVersion {

    const val ANDROID_12_L = Build.VERSION_CODES.S_V2
    const val ANDROID_12 = Build.VERSION_CODES.S
    const val ANDROID_11 = Build.VERSION_CODES.R
    const val ANDROID_10 = Build.VERSION_CODES.Q
    const val ANDROID_9 = Build.VERSION_CODES.P
    const val ANDROID_8_1 = Build.VERSION_CODES.O_MR1
    const val ANDROID_8 = Build.VERSION_CODES.O
    const val ANDROID_7_1 = Build.VERSION_CODES.N_MR1
    const val ANDROID_7 = Build.VERSION_CODES.N
    const val ANDROID_6 = Build.VERSION_CODES.M

    @ChecksSdkIntAtLeast(api = ANDROID_12)
    fun isAndroid12(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_12
    }

    @ChecksSdkIntAtLeast(api = ANDROID_11)
    fun isAndroid11(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_11
    }

    @ChecksSdkIntAtLeast(api = ANDROID_10)
    fun isAndroid10(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_10
    }

    @ChecksSdkIntAtLeast(api = ANDROID_9)
    fun isAndroid9(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_9
    }

    @ChecksSdkIntAtLeast(api = ANDROID_8)
    fun isAndroid8(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_8
    }

    @ChecksSdkIntAtLeast(api = ANDROID_7)
    fun isAndroid7(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_7
    }

    @ChecksSdkIntAtLeast(api = ANDROID_6)
    fun isAndroid6(): Boolean {
        return Build.VERSION.SDK_INT >= ANDROID_6
    }
}