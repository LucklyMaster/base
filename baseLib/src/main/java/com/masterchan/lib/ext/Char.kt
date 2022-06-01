package com.masterchan.lib.ext

/**
 * @author MasterChan
 * @date 2021-12-21 11:35
 * @describe Char
 */

fun Char.hex2Int(): Int {
    return when (this) {
        in '0'..'9' -> {
            this - '0'
        }
        in 'A'..'F' -> {
            this - 'A' + 10
        }
        else -> {
            throw IllegalArgumentException()
        }
    }
}