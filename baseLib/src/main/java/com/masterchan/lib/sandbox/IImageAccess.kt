package com.masterchan.lib.sandbox

/**
 * 媒体文件访问接口
 * @author: MasterChan
 * @date: 2022-06-09 23:32
 */
interface IImageAccess : IFileAccess {

    fun queryLessDuration(publicDir: String, duration: Long)

    fun queryOverDuration(publicDir: String, duration: Long)
}