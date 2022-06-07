package com.masterchan.lib.log

import com.masterchan.lib.BuildConfig
import com.masterchan.lib.ext.Log
import org.json.JSONArray
import org.json.JSONObject

/**
 * Log打印工具，可以使用[Log]类型别名代替[MLog]
 * @author: MasterChan
 * @date: 2022-05-28 23:20
 */
object MLog {
    private var tag = "MLog"
    private var debug = true
    private var saveLog = BuildConfig.DEBUG
    private val printer = MPrinter()
    private var printStrategy: IPrintStrategy = MPrintStrategy(printer)
    private var logManager: AbsLogManager? = null
    private var logManagerConfig: LogManagerConfig? = null

    fun setTag(tag: String) = apply {
        this.tag = tag
    }

    fun setDebug(debug: Boolean) = apply {
        this.debug = debug
    }

    fun saveLog(saveLog: Boolean) = apply {
        this.saveLog = saveLog
    }

    fun setPrintStrategy(printStrategy: IPrintStrategy) = apply {
        this.printStrategy = printStrategy
    }

    fun setLogManager(logManager: AbsLogManager) = apply {
        this.logManager = logManager
        logManagerConfig?.let { logManager.config = it }
    }

    fun setLogManagerConfig(config: LogManagerConfig) = apply {
        this.logManagerConfig = config
        logManager?.config = config
    }

    fun setLogManagerConfig(method: LogManagerConfig?.() -> Unit) = apply {
        method.invoke(logManagerConfig)
        logManagerConfig?.let { logManager?.config = it }
    }

    fun v(any: Any?) {
        print(Priority.VERBOSE, tag, any, null)
    }

    fun v(tag: String, any: Any?, tr: Throwable? = null) {
        print(Priority.VERBOSE, tag, any, tr)
    }

    fun d(any: Any?) {
        print(Priority.DEBUG, tag, any, null)
    }

    fun d(tag: String, any: Any?, tr: Throwable? = null) {
        print(Priority.DEBUG, tag, any, tr)
    }

    fun i(any: Any?) {
        print(Priority.INFO, tag, any, null)
    }

    fun i(tag: String, any: Any?, tr: Throwable? = null) {
        print(Priority.INFO, tag, any, tr)
    }

    fun w(any: Any?) {
        print(Priority.WARN, tag, any, null)
    }

    fun w(tag: String, any: Any?, tr: Throwable? = null) {
        print(Priority.WARN, tag, any, tr)
    }

    fun e(any: Any?) {
        print(Priority.ERROR, tag, any, null)
    }

    fun e(tag: String, any: Any?, tr: Throwable? = null) {
        print(Priority.ERROR, tag, any, tr)
    }

    fun wtf(any: Any?) {
        print(Priority.ASSERT, tag, any, null)
    }

    fun wtf(tag: String, any: Any?, tr: Throwable? = null) {
        print(Priority.ASSERT, tag, any, tr)
    }

    private fun print(priority: Int, tag: String, any: Any?, tr: Throwable?) {
        val content = getContent(any, tr)
        if (debug) {
            printStrategy.println(priority, tag, content, tr)
        }
        if (saveLog) {
            logManager?.onPrint(content)
        }
    }

    private fun getContent(any: Any?, tr: Throwable?): String {
        if (any == null) {
            return if (tr == null) "Notice! The print content is null." else ""
        }
        return when (any) {
            is String -> parseString(any)
            is Array<*> -> any.contentToString()
            is Map<*, *> -> parseString(JSONObject(any).toString())
            else -> any.toString()
        }
    }

    private fun parseString(content: String): String {
        return when {
            content.startsWith("{") && content.endsWith("}") -> {
                try {
                    JSONObject(content).toString(2)
                } catch (e: Exception) {
                    content
                }
            }
            content.startsWith("[") && content.endsWith("]") -> {
                try {
                    JSONArray(content).toString(2)
                } catch (e: Exception) {
                    content
                }
            }
            else -> content
        }
    }
}