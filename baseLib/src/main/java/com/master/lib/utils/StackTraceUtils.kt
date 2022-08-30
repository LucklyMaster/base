package com.master.lib.utils

/**
 * 调用堆栈工具类
 * @author: MasterChan
 * @date: 2022-06-23 22:33
 */
internal object StackTraceUtils {
    /**
     * 获取当前的调用堆栈
     * @return StackTraceElement?
     */
    @JvmStatic
    fun getTargetStackTraceElement(className: String): StackTraceElement? {
        var traceElement: StackTraceElement? = null
        var shouldTrace = false
        val stackTrace = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTrace) {
            val isLogMethod = stackTraceElement.className == className
            if (shouldTrace && !isLogMethod) {
                traceElement = stackTraceElement
                break
            }
            shouldTrace = isLogMethod
        }
        return traceElement
    }
}