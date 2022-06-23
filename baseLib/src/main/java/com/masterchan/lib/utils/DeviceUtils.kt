package com.masterchan.lib.utils

import android.annotation.SuppressLint
import android.content.Context.TELEPHONY_SERVICE
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.masterchan.lib.ext.application
import java.io.File
import java.lang.reflect.Method


object DeviceUtils {

    @SuppressLint("HardwareIds")
    fun getAndroidId() {
        val id = Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun getImei(): String? {
        var imei = ""
        try {=
            val tm = application.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            imei =
                val method: Method = tm.javaClass.getMethod("getImei")
                method.invoke(tm)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imei
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