package com.masterchan.lib.log

import com.masterchan.lib.utils.DateUtils.toString
import com.masterchan.lib.utils.FileUtils
import java.util.*

/**
 * @author: MasterChan
 * @date: 2022-05-29 22:42
 * @describe:实现日志管理
 */
open class MLogManagement : ILogManager {

    override lateinit var printer: IPrinter
    override lateinit var config: LogManagerConfig
    override var isInit = false
    private var mpLog = MPLog()
    private var mpHandle: Long? = null

    override fun init() {
        val fileName = "${Date().toString("yyyyMMddHHmmss")}.${config.fileSuffix}"
        val cachePath = "${config.cacheDir}/$fileName"
        val logPath = "${config.logDir}/$fileName"
        if (!FileUtils.isFileExist(cachePath)) {
            FileUtils.createFile(cachePath)
        }
        if (!FileUtils.isFileExist(logPath)) {
            FileUtils.createFile(logPath)
        }
        val handle = mpLog.init(cachePath, logPath, getCacheSize())
        if (handle != -1L) {
            isInit = true
            mpHandle = handle
        }
    }

    open fun getCacheSize() = 4096 * 4

    override fun saveLog(content: String) {
        mpHandle?.let {
            mpLog.write(it, getLog(content), true)
        }
    }

    open protected fun getLog(content: String): String {
        var newContent = content
        if (!content.endsWith("\n") && !content.endsWith("\n\r")) {
            newContent += "\n"
        }
        newContent.plus("-------------------------------------------------\n")

        var header = Date().toString("yyyy-MM-dd HH:mm:ss:SS ")
        val element = getTargetStackTraceElement() ?: return header.plus("\n").plus(newContent)
        val className = element.className.substringAfterLast(".")
        header += " $className.${element.methodName}(${element.fileName}:${element.lineNumber})\n"
        return header + newContent
    }

    /**
     * 获取当前的调用堆栈
     * @return StackTraceElement?
     */
    protected fun getTargetStackTraceElement(): StackTraceElement? {
        var traceElement: StackTraceElement? = null
        var shouldTrace = false
        val stackTrace = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTrace) {
            val isLogMethod = stackTraceElement.className == MLog.javaClass.name
            if (shouldTrace && !isLogMethod) {
                traceElement = stackTraceElement
                break
            }
            shouldTrace = isLogMethod
        }
        return traceElement
    }

    override fun release() {
        mpHandle?.let { mpLog.release(it) }
    }
}