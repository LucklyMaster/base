package com.master.lib.ext

import android.app.Activity
import android.content.Intent
import android.view.ViewGroup

/**
 * 获取当前Activity的ContentView
 */
fun Activity.contentView(): ViewGroup = findViewById(android.R.id.content)