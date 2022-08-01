package com.master.log

import android.util.Log
import com.master.ext.create
import com.master.utils.DateUtils
import com.master.utils.DateUtils.toString
import com.master.utils.StackTraceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * 实现日志管理，此日志管理，未实现限制单个日志文件最大字节数，日志压缩、加密，异步写入等；
 * 紧针对简单场景下的日志记录，更多场景需要另外实现
 * @author: MasterChan
 * @date: 2022-05-29 22:42
 */
open class DiskLogManager : AbsLogManager() {

    /**
     * 默认cache文件大小为120K
     */
    protected open var cacheSize = 4096 * 30

    private var mpHandle: Long? = null

    companion object {
        init {
            System.loadLibrary("mpLog")
        }
    }

    init {
        this.init()
    }

    override fun init() {
        GlobalScope.launch(Dispatchers.IO) {
            File(config.logDir).listFiles { file -> file.isFile }?.forEach {
                if (DateUtils.dayDiff(Date(it.lastModified()), Date()) > config.saveDays) {
                    it.delete()
                }
            }
            val fileName = "${Date().toString("yyyyMMdd")}.${config.fileSuffix}"
            val cachePath = "${config.cacheDir}/$fileName"
            val logPath = "${config.logDir}/$fileName"
            File(cachePath).create()
            File(logPath).create()
            val handle = init(cachePath, logPath, cacheSize)
            if (handle != -1L) {
                isInit = true
                mpHandle = handle
            }
            Log.d("MLog", "DiskLogManager init ${if (isInit) "success" else "failed:$handle"}")
        }
    }

    override fun onPrint(content: String) {
        mpHandle?.let {
            write(it, getLog(content), true)
        }
    }

    protected open fun getLog(content: String): String {
        var newContent = content
        if (!content.endsWith("\n") && !content.endsWith("\n\r")) {
            newContent += "\n"
        }
        newContent += "-------------------------------------------------\n"

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
    protected open fun getTargetStackTraceElement(): StackTraceElement? {
        return StackTraceUtils.getTargetStackTraceElement(MLog.javaClass.name)
    }

    override fun release() {
        mpHandle?.let {
            isInit = false
            mpHandle = null
            release(it)
        }
    }

    /**
     * 初始化
     * @param cachePath 日志路径
     * @param logPath 日志路径
     * @param cacheSize 缓存大小，必须是4096的整数倍
     * @return handle
     */
    private external fun init(cachePath: String, logPath: String, cacheSize: Int): Long

    /**
     * 将日志写入到缓存中，当缓存文件大小超过cacheSize时，同步到磁盘
     * @param handle 句柄
     * @param text 日志
     * @param append 是否是追加写入
     */
    private external fun write(handle: Long, text: String, append: Boolean = true)

    /**
     * 释放资源，同时会将缓存文件同步到磁盘
     * @param handle Long
     */
    private external fun release(handle: Long)
}