package com.masterchan.lib.sandbox

import com.masterchan.lib.sandbox.access.IFileRequest

/**
 * 媒体文件访问接口
 * @author: MasterChan
 * @date: 2022-06-09 23:32
 */
interface IAudioRequest : IFileRequest {

    fun queryLessDuration(publicDir: String, duration: Long)

    fun queryOverDuration(publicDir: String, duration: Long)
}