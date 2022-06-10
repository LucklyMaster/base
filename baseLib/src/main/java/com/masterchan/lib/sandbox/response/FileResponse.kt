package com.masterchan.lib.sandbox.response

import android.net.Uri
import java.io.File

/**
 * API29以下的访问结果
 * @author: MasterChan
 * @date: 2022-06-09 14:50
 */
open class FileResponse(var uri: Uri, var file: File, var path: String, var size: Long) {

    open var addDate = -1L
        get() = if (field == -1L) getCreateTime().also { field = it } else field

    private fun getCreateTime(): Long {
        return 0
    }
}