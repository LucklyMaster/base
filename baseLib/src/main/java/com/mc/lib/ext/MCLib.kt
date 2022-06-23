package com.mc.lib.ext

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Environment
import com.mc.lib.ActivityStack
import com.mc.lib.MCLib
import com.mc.lib.log.MLog
import com.mc.lib.log.Priority
import com.mc.lib.utils.StackTraceUtils

var application: Application = MCLib.application

typealias Log = MLog

/**
 * APP是否在前台
 */
val isAppForeground
    get() = ActivityStack.instance.isAppForeground

/**
 * 获取栈顶的Activity
 * @return Activity?
 */
val topActivity: Activity?
    get() = ActivityStack.instance.currentActivity

/**
 * 是否适配了分区存储
 * @return Boolean
 */
val isScopedStorage: Boolean
    get() = Build.VERSION.SDK_INT >= 29 && !Environment.isExternalStorageLegacy()

fun Any.println() {
    MLog.print(
        Priority.DEBUG, MLog.tag, this,
        StackTraceUtils.getTargetStackTraceElement("com.mc.lib.ext.MCLibKt")
    )
}