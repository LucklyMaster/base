package com.master.lib.ext

import java.util.*

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

fun String.hexString2ByteArray(): ByteArray {
    var hexString = this
    var len = hexString.length
    if (len % 2 != 0) {
        hexString = "0$hexString"
        len += 1
    }
    val hexBytes = hexString.uppercase(Locale.getDefault()).toCharArray()
    val ret = ByteArray(len shr 1)
    var i = 0
    while (i < len) {
        ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
        i += 2
    }
    return ret
}

private fun hex2Dec(hexChar: Char): Int {
    return when (hexChar) {
        in '0'..'9' -> {
            hexChar - '0'
        }
        in 'A'..'F' -> {
            hexChar - 'A' + 10
        }
        else -> {
            throw IllegalArgumentException()
        }
    }
}