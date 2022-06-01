package com.masterchan.lib.ext

import android.app.Activity
import android.content.Intent
import android.view.ViewGroup

/**
 * @author MasterChan
 * @date 2021-12-06 14:00
 * @describe Activity相关的扩展函数
 */

/**
 * 获取当前Activity的ContentView
 */
val Activity.contentView: ViewGroup
    get() {
        return findViewById(android.R.id.content)
    }

fun Activity.startActivity(clazz: Class<out Activity>) {
    startActivity(Intent(this, clazz))
}

inline fun <reified T : Activity> Activity.startActivity(params: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    params.invoke(intent)
    startActivity(intent)
}