package com.masterchan.lib.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.annotation.StringRes

/**
 * 屏幕宽度
 */
val Context.screenWidth: Int
    get() {
        return resources.displayMetrics.widthPixels
    }

/**
 * 屏幕高度
 */
val Context.screenHeight: Int
    get() {
        return resources.displayMetrics.heightPixels
    }

/**
 * 状态栏高度
 */
val Context.statusBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

/**
 * 导航栏高度
 */
val Context.navigationBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

/**
 * 屏幕方向，取值为[Configuration.ORIENTATION_LANDSCAPE]、[Configuration.ORIENTATION_PORTRAIT]、
 * [Configuration.ORIENTATION_UNDEFINED]
 */
val Context.orientation: Int?
    get() {
        return resources?.configuration?.orientation
    }

/**
 * 是否是竖屏
 */
val Context.isPortrait: Boolean
    get() {
        return orientation == Configuration.ORIENTATION_PORTRAIT
    }

/**
 * 版本号
 */
val Context.versionCode: Long
    get() {
        return packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
    }

/**
 * 版本名
 */
val Context.versionName: String
    get() {
        return packageManager.getPackageInfo(packageName, 0).versionName
    }

/**
 * 获取String集合
 * @receiver Context
 * @param resources StringRes
 * @return List<String>
 */
fun Context.getStringList(@StringRes vararg resources: Int): List<String> {
    return resources.map { getString(it) }
}

/**
 * 获取String数组
 * @receiver Context
 * @param resources IntArray
 * @return Array<String>
 */
fun Context.getStringArray(@StringRes vararg resources: Int): Array<String> {
    return getStringList(*resources).toTypedArray()
}

/**
 * 获取Context对应的Activity
 * @receiver Context
 * @return Activity?
 */
fun Context.toActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = (this as ContextWrapper).baseContext
    }
    return null
}