package com.masterchan.lib.log

import android.util.Log
import com.masterchan.lib.utils.FileUtils
import com.masterchan.mmap.MPLog

/**
 * @author: MasterChan
 * @date: 2022-05-29 22:42
 * @describe:实现保存日志的接口
 */
open class MLogManagement : ILogManager {

    private var mpFile = MPLog()
    private var mpFileHandle: Long? = null

    init {
        val filePath = "$fileDir/a.$fileSuffix"
        val filePath2 = "$fileDir/b.$fileSuffix"
        if (!FileUtils.isFileExist(filePath)) {
            FileUtils.createFile(filePath)
        }
        if (!FileUtils.isFileExist(filePath2)) {
            FileUtils.createFile(filePath2)
        }
        val handle = mpFile.init(filePath, filePath2, 4096 * 4)
        if (handle != -1L) {
            mpFileHandle = handle
        }
        Log.d("TAG", "handle = $handle")
    }

    var count = 0
    override fun saveLog(content: String?) {
        count++
        mpFileHandle?.let { mpFile.write(it, content!!, true) }
    }

}