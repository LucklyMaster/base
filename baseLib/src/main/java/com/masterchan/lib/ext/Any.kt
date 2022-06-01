package com.masterchan.lib.ext

import android.app.Activity
import android.os.Looper
import android.view.View
import com.masterchan.lib.ActivityStack

/**
 * @author MasterChan
 * @date 2021-12-16 16:17
 * @describe Any
 */

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