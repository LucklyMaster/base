package com.masterchan.lib.ext

inline fun <T> Array<T>.isNotEmpty(method: Array<T>.() -> Unit) = apply {
    if (isNotEmpty()) method(this)
}

inline fun <T> Array<T>.isEmpty(method: Array<T>.() -> Unit) = apply {
    if (isEmpty()) method(this)
}
