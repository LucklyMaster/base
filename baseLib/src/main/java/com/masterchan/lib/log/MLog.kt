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
    var tag = "MLog"
        private set
    private var debug = BuildConfig.DEBUG
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
        print(Priority.VERBOSE, tag, any)
    }

    fun v(tag: String, any: Any?) {
        print(Priority.VERBOSE, tag, any)
    }

    fun d(any: Any?) {
        print(Priority.DEBUG, tag, any)
    }

    fun d(tag: String, any: Any?) {
        print(Priority.DEBUG, tag, any)
    }

    fun i(any: Any?) {
        print(Priority.INFO, tag, any)
    }

    fun i(tag: String, any: Any?) {
        print(Priority.INFO, tag, any)
    }

    fun w(any: Any?) {
        print(Priority.WARN, tag, any)
    }

    fun w(tag: String, any: Any?) {
        print(Priority.WARN, tag, any)
    }

    fun e(any: Any?) {
        print(Priority.ERROR, tag, any)
    }

    fun e(tag: String, any: Any?) {
        print(Priority.ERROR, tag, any)
    }

    fun wtf(any: Any?) {
        print(Priority.ASSERT, tag, any)
    }

    fun wtf(tag: String, any: Any?) {
        print(Priority.ASSERT, tag, any)
    }

    private fun print(priority: Int, tag: String, any: Any?) {
        val content = parseAny(any)
        if (debug) {
            printStrategy.println(priority, tag, content)
        }
        if (saveLog) {
            logManager?.onPrint(content)
        }
    }

    /**
     * 将Any解析为String
     * @param any Any?
     * @return String
     */
    private fun parseAny(any: Any?): String {
        if (any == null) {
            return "Notice! The print content is null."
        }
        return when (any) {
            is String -> parseString(any)
            is Array<*> -> any.contentToString()
            is Map<*, *> -> parseString(JSONObject(any).toString())
            is Throwable -> android.util.Log.getStackTraceString(any)
            else -> any.toString()
        }
    }

    /**
     * 解析String，判断是否是json格式
     * @param content String
     * @return String
     */
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