package com.master.lib.ext

import android.app.Activity
import android.content.Intent
import android.view.ViewGroup

/**
 * 获取当前Activity的ContentView
 */
fun Activity.contentView(): ViewGroup = findViewById(android.R.id.content)

fun Activity.startActivity(clazz: Class<out Activity>) {
    startActivity(Intent(this, clazz))
}

inline fun <reified T : Activity> Activity.startActivity(params: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    params.invoke(intent)
    startActivity(intent)
}