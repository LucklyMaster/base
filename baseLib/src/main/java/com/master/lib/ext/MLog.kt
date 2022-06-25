package com.master.lib.ext

import com.master.lib.log.MLog
import com.master.lib.log.Priority
import com.master.lib.utils.StackTraceUtils

fun Any.println(priority: Int = Priority.DEBUG) {
    MLog.print(
        priority, MLog.tag, this,
        StackTraceUtils.getTargetStackTraceElement("com.mc.lib.ext.MLogKt")
    )
}

fun Any.logV() {
    println(Priority.VERBOSE)
}

fun Any.logD() {
    println(Priority.DEBUG)
}

fun Any.logI() {
    println(Priority.INFO)
}

fun Any.logW() {
    println(Priority.WARN)
}

fun Any.logE() {
    println(Priority.ERROR)
}

fun Any.logA() {
    println(Priority.ASSERT)
}