package com.mc.lib.log

/**
 * 打印机接口定义
 * @author: MasterChan
 * @date: 2022-05-28 23:34
 */
interface IPrinter {
    /**
     * 打印信息
     * @param priority 打印等级
     * @param tag TAG
     * @param content 需要打印的信息
     */
    fun println(priority: Int, tag: String, content: String)
}