package com.master.ext

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Environment
import com.master.ActivityStack
import com.master.MCLib
import com.master.log.MLog

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