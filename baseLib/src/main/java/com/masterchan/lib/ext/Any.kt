package com.masterchan.lib.ext

import android.app.Activity
import android.os.Looper
import android.view.View
import com.masterchan.lib.ActivityStack

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
fun getTopActivity(): Activity? {
    return ActivityStack.instance.currentActivity
}

fun setOnClickListeners(clickListener: View.OnClickListener, vararg views: View) {
    views.forEach { it.setOnClickListener(clickListener) }
}