package com.masterchan.lib.log

/**
 * @author: MasterChan
 * @date: 2022-05-29 22:40
 * @describe: 日志管理接口
 */
interface ILogManager {

    var printer: IPrinter

    var config: LogManagerConfig

    var isInit: Boolean

    fun init()

    fun saveLog(content: String)

    fun release()
}