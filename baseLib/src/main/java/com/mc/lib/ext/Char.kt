package com.mc.lib.ext

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