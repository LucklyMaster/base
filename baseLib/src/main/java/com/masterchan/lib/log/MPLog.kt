package com.masterchan.lib.log

/**
 * @author: MasterChan
 * @date: 2022-05-28 20:49
 * @describe:memory mapping写文件
 */
class MPLog {
    companion object {
        init {
            System.loadLibrary("mpLog")
        }
    }

    /**
     * 初始化
     * @param cachePath 日志路径
     * @param logPath 日志路径
     * @param cacheSize 缓存大小，必须是4096的整数倍
     * @return handle
     */
    external fun init(cachePath: String, logPath: String, cacheSize: Int): Long

    /**
     * 将日志写入到缓存中，当缓存文件大小超过cacheSize时，同步到磁盘
     * @param handle 句柄
     * @param text 日志
     * @param append 是否是追加写入
     */
    external fun write(handle: Long, text: String, append: Boolean = true)

    /**
     * 释放资源，同时会将缓存文件同步到磁盘
     * @param handle Long
     */
    external fun release(handle: Long)
}