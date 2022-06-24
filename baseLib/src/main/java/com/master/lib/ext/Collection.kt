package com.master.lib.ext

inline fun <T> Collection<T>.isNotEmpty(method: Collection<T>.() -> Unit) = apply {
    if (isNotEmpty()) method(this)
}

inline fun <T> Collection<T>.isEmpty(method: Collection<T>.() -> Unit) = apply {
    if (isEmpty()) method(this)
}
