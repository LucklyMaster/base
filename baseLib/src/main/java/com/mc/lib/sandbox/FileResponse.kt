package com.mc.lib.sandbox

import android.net.Uri
import java.io.File

/**
 * 文件访问结果，在API29以下只有[uri]、[file]、[addDate]有效，对于其他字段，可以使用
 * [android.media.MediaMetadataRetriever]获取
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