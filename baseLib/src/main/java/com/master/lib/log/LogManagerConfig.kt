package com.master.lib.log

import com.master.lib.ext.application

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
    var logDir: String = "${application.filesDir.absolutePath}/MCLib/MLog",
    /**
     * 日志文件后缀名
     */
    var fileSuffix: String = "log",
    /**
     * 保存的天数
     */
    var saveDays: Int = 10
)
