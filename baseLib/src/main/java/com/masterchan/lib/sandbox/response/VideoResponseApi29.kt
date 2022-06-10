package com.masterchan.lib.sandbox.response

import android.net.Uri
import java.io.File

/**
 * API29以上视频访问
 * @author: MasterChan
 * @date: 2022-06-10 16:15
 */
open class VideoResponseApi29(
    uri: Uri,
    file: File,
    path: String,
    length: Long,
    addDate: Long,
    override var duration: Long,
    override var width: Int,
    override var height: Int
) : VideoResponse(uri, file, path, length, addDate)