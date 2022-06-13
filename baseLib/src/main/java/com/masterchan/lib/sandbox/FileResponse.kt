package com.masterchan.lib.sandbox

import android.net.Uri
import java.io.File

/**
 * 文件访问结果，在API29以下只有[file]有效
 * @author: MasterChan
 * @date: 2022-06-11 22:31
 */
data class FileResponse(
    var uri: Uri,
    var file: File,
    var addDate: Long = NOT_SUPPORT,
    var duration: Long = NOT_SUPPORT,
    var width: Int = NOT_SUPPORT.toInt(),
    var height: Int = NOT_SUPPORT.toInt()
) {
    companion object {
        const val NOT_SUPPORT = -2L
    }
}