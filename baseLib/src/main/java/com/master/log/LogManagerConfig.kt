package com.master.log

import com.master.ext.application

/**
 * 日志管理配置文件
 * @author: MasterChan
 * @date: 2022-06-07 10:52
 */
data class LogManagerConfig(
    /**
     * 日志缓存目录
     */
    var cacheDir: String = "${application.cacheDir.absolutePath}/MCLib/MLog",
    /**
     * 日志保存目录
     */
    val logDir: String = "${application.filesDir.absolutePath}/MCLib/MLog",
    /**
     * 日志文件后缀名
     */
    val fileSuffix: String = "log",
    /**
     * 保存的天数
     */
    val saveDays: Int = 10
)
