package com.master.lib.ext

import android.content.Context
import androidx.annotation.StringRes
import java.util.*

fun String.toHexByteArray(): ByteArray {
    require(length % 2 == 0) { "长度不是偶数" }
    val hexBytes = uppercase(Locale.getDefault()).toCharArray()
    val ret = ByteArray(length ushr 1)
    var i = 0
    while (i < length) {
        ret[i shr 1] = (hexBytes[i].hex2Int() shl 4 or hexBytes[i + 1].hex2Int()).toByte()
        i += 2
    }
    return ret
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