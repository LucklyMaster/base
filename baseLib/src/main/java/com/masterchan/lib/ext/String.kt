package com.masterchan.lib.ext

import java.util.*

/**
 * @author MasterChan
 * @date 2021-12-21 11:20
 * @describe String
 */

fun ByteArray.toHexString(): String {
    val ret = CharArray(size shl 1)
    var i = 0
    var j = 0
    val hexDigits = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F'
    )
    while (i < size) {
        ret[j++] = hexDigits[get(i).toInt() ushr 4 and 0x0f]
        ret[j++] = hexDigits[get(i).toInt() and 0x0f]
        i++
    }
    return String(ret)
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