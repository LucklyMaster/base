package com.masterchan.lib.log

import com.masterchan.lib.MCLib

/**
 * @author: MasterChan
 * @date: 2022-05-29 22:40
 * @describe: 日志管理接口
 */
interface ILogManager {

    /**
     * 文件保存目录
     */
    val fileDir: String
        get() = "${MCLib.application.filesDir.absolutePath}/MLog"

    /**
     * 文件后缀名
     */
    val fileSuffix: String
        get() = "log"

    /**
     * 保存的天数
     */
    val saveDays: Int
        get() = 10

    fun saveLog(content: String?)
}