package com.masterchan.lib.ext

import android.app.Activity
import android.app.Application
import android.os.Looper
import android.view.View
import com.masterchan.lib.ActivityStack
import com.masterchan.lib.MCLib

var application: Application = MCLib.application

fun dp2px(dp: Float): Float = (dp * application.resources.displayMetrics.density + 0.5f)

fun px2dp(px: Float): Float = (px / application.resources.displayMetrics.density + 0.5f)

fun dp2pxi(dp: Float): Int = (dp * application.resources.displayMetrics.density + 0.5f).toInt()

fun px2dpi(px: Float): Int = (px / application.resources.displayMetrics.density + 0.5f).toInt()

/**
 * 当前是否是主线程
 */
fun isMainThread() = Thread.currentThread() === Looper.getMainLooper().thread

/**
 * APP是否在前台
 */
fun isAppForeground() = ActivityStack.instance.isAppForeground

/**
 * 获取栈顶的Activity
 * @return Activity?
 */
fun getTopActivity(): Activity? = ActivityStack.instance.currentActivity

fun setOnClickListeners(clickListener: View.OnClickListener, vararg views: View) {
    views.forEach { it.setOnClickListener(clickListener) }
}