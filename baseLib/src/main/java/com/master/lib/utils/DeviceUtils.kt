package com.master.lib.utils

import android.annotation.SuppressLint
import android.content.Context.TELEPHONY_SERVICE
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.master.lib.ext.application
import java.io.File
import java.lang.reflect.Method

/**
 * DeviceUtils
 * @author: MasterChan
 * @date: 2022-6-23 21:02
 */
object DeviceUtils {

    @SuppressLint("HardwareIds")
    fun getAndroidId(): String {
        return Settings.Secure.getString(
            application.contentResolver, Settings.Secure.ANDROID_ID
        ) ?: ""
    }

    fun getImei(): String? {
        try {
            val tm = application.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val method: Method = tm.javaClass.getMethod("getImei")
            return method.invoke(tm)?.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getProduct(): String {
        return Build.MANUFACTURER
    }

    fun isRooted(): Boolean {
        val su = "su"
        val locations = arrayOf(
            "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
            "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
            "/system/sbin/", "/usr/bin/", "/vendor/bin/"
        )
        locations.forEach {
            if (File(it + su).exists()) return true
        }
        return false
    }
}