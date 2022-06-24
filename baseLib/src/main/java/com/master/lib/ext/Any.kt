package com.master.lib.ext

import android.os.Looper
import android.view.View

/**
 * 当前是否是主线程
 */
fun isMainThread() = Thread.currentThread() === Looper.getMainLooper().thread

fun setOnClickListeners(clickListener: View.OnClickListener, vararg views: View) {
    views.forEach { it.setOnClickListener(clickListener) }
}

inline fun <T> T.orNull(condition: T.() -> Boolean): T? {
    return if (condition.invoke(this)) return this else null
}