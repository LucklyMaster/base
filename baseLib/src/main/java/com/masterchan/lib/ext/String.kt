package com.masterchan.lib.ext

import android.content.Context
import androidx.annotation.StringRes

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