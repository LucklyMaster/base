package com.masterchan.lib.log

import com.masterchan.lib.MCLib

/**
 * 日志管理配置文件
 * @author: MasterChan
 * @date: 2022-06-07 10:52
 */
data class LogManagerConfig(
    /**
     * 日志缓存目录
     */
    var cacheDir: String = "${MCLib.application.cacheDir.absolutePath}/MLog",
    /**
     * 日志保存目录
     */
    val logDir: String = "${MCLib.application.filesDir.absolutePath}/MLog",
    /**
     * 日志文件后缀名
     */
    val fileSuffix: String = "log",
    /**
     * 保存的天数
     */
    val saveDays: Int = 10
)
