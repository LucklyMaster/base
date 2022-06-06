package com.masterchan.lib.log

/**
 * @author: MasterChan
 * @date: 2022-06-06 22:48
 * @describe: 日志管理
 */
abstract class AbsLogManager {
    var isInit = false

    var config = LogManagerConfig()

    abstract fun init()

    abstract fun onPrint(content: String)

    abstract fun release()
}