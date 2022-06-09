package com.masterchan.lib.ext

import android.app.Activity
import android.app.Application
import android.os.Looper
import android.view.View
import com.masterchan.lib.ActivityStack
import com.masterchan.lib.MCLib

/**
 * 当前是否是主线程
 */
fun isMainThread() = Thread.currentThread() === Looper.getMainLooper().thread

fun setOnClickListeners(clickListener: View.OnClickListener, vararg views: View) {
    views.forEach { it.setOnClickListener(clickListener) }
}