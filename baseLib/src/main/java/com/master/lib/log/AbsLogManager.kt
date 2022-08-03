package com.master.lib.log

/**
 * 日志管理
 * @author: MasterChan
 * @date: 2022-06-06 22:48
 */
abstract class AbsLogManager {
    var isInit = false

    var config = LogManagerConfig()

    abstract fun init()

    abstract fun onPrint(content: String)

    abstract fun release()
}