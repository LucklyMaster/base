package com.masterchan.lib.ext

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Environment
import com.masterchan.lib.ActivityStack
import com.masterchan.lib.MCLib
import com.masterchan.lib.log.MLog

var application: Application = MCLib.application

typealias Log = MLog

/**
 * APP是否在前台
 */
fun isAppForeground() = ActivityStack.instance.isAppForeground

/**
 * 获取栈顶的Activity
 * @return Activity?
 */
fun getTopActivity(): Activity? = ActivityStack.instance.currentActivity

/**
 * 是否适配了分区存储
 * @return Boolean
 */
val scopedStorage: Boolean
    get() = Build.VERSION.SDK_INT >= 29 && !Environment.isExternalStorageLegacy()