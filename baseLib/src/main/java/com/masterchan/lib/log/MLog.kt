package com.masterchan.lib.log

import com.masterchan.lib.BuildConfig
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author: MasterChan
 * @date: 2022-05-28 23:20
 * @describe: Log打印工具
 */
object MLog {
    private var tag = "MLog"
    private var debug = true
    private var saveLog = BuildConfig.DEBUG
    private var printStrategy: IPrintStrategy = MPrintStrategy(MPrinter())
    private var logManager: ILogManager? = null

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

    fun setLogManager(logManager: ILogManager) = apply {
        this.logManager = logManager
    }

    fun v(any: Any?, tr: Throwable? = null) {
        printLog(Priority.VERBOSE, any, tr)
    }

    fun d(any: Any?, tr: Throwable? = null) {
        printLog(Priority.DEBUG, any, tr)
    }

    fun i(any: Any?, tr: Throwable? = null) {
        printLog(Priority.INFO, any, tr)
    }

    fun w(any: Any?, tr: Throwable? = null) {
        printLog(Priority.WARN, any, tr)
    }

    fun e(any: Any?, tr: Throwable? = null) {
        printLog(Priority.ERROR, any, tr)
    }

    fun wtf(any: Any?, tr: Throwable? = null) {
        printLog(Priority.ASSERT, any, tr)
    }

    private fun printLog(priority: Int, any: Any?, tr: Throwable?) {
        val content = getContent(any)
        if (debug) {
            printStrategy.println(priority, tag, content, tr)
        }
        if (saveLog) {
            if (logManager == null) {
                logManager = MLogManagement()
            }
            logManager!!.saveLog(content)
        }
    }

    private fun getContent(any: Any?): String? {
        if (any == null) {
            return null
        }
        return when (any) {
            is String -> parseString(any)
            is Array<*> -> any.contentToString()
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