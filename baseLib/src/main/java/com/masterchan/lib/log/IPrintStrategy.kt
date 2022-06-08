package com.masterchan.lib.log

/**
 * 打印策略接口定义
 * @author: MasterChan
 * @date: 2022-05-29 12:40
 */
interface IPrintStrategy {
    /**
     * 实现自己的打印策略
     * @param priority 打印等级
     * @param tag TAG
     * @param content 需要打印的信息
     */
    fun println(priority: Int, tag: String, content: String)
}