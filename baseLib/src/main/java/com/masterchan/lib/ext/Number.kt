package com.masterchan.lib.ext

/**
 * @author MasterChan
 * @date 2022-01-05 15:45
 * @describe Number
 */
fun Double.isInt(): Boolean {
    return this % 1 == 0.0 && this <= Int.MAX_VALUE
}

fun Float.isInt(): Boolean {
    return this % 1 == 0.0f && this <= Int.MAX_VALUE
}