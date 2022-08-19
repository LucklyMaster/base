package com.master.lib.ext

import android.content.Context
import android.util.Base64
import androidx.annotation.StringRes
import com.master.lib.utils.EncryptUtils
import java.util.*

fun String.toBase64(flags: Int = Base64.NO_WRAP): ByteArray {
    return EncryptUtils.base64(this.toByteArray(), flags)
}

fun String.toBase64String(flags: Int = Base64.NO_WRAP): String {
    return EncryptUtils.base64(this, flags)
}

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

/**
 * 判断是否是手机号码
 * @receiver String
 * @return Boolean
 */
fun String.isPhoneNum(): Boolean {
    return "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}\$".toRegex()
        .matches(this)
}

/**
 * 判断是否是邮箱
 * @receiver String
 * @return Boolean
 */
fun String.isEmail(): Boolean {
    return "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\$".toRegex().matches(this)
}

/**
 * 判断是否是域名
 * @receiver String
 * @return Boolean
 */
fun String.isDomain(): Boolean {
    return "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?".toRegex()
        .matches(this)
}

/**
 * 判断是否是URL地址
 * @receiver String
 * @return Boolean
 */
fun String.isInternetUrl(): Boolean {
    return "[a-zA-z]+://[^\\s]*".toRegex().matches(this)
}

/**
 * 判断是否是身份证号
 * @receiver String
 * @return Boolean
 */
fun String.isIdCardNum(): Boolean {
    return "(^\\d{15}\$)|(^\\d{18}\$)|(^\\d{17}(\\d|X|x)\$)".toRegex().matches(this)
}