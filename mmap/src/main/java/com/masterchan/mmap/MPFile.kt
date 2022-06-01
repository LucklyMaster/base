package com.masterchan.mmap

/**
 * @author: MasterChan
 * @date: 2022-05-28 20:49
 * @describe:memory mapping写文件
 */
class MPFile {
    companion object {
        init {
            System.loadLibrary("mpFile")
        }
    }

    external fun open(cachePath: String, logPath: String = ""): Long

    external fun write(handle: Long, text: String, append: Boolean = false)

    external fun read(handle: Long): String

    external fun close(handle: Long)
}