package com.masterchan.lib.sandbox.response

import android.net.Uri
import android.util.Size
import com.masterchan.lib.utils.ImageUtils
import java.io.File

/**
 * API29以下视频访问
 * @author: MasterChan
 * @date: 2022-06-10 16:15
 */
open class VideoResponse(uri: Uri, file: File, path: String, length: Long, addDate: Long) :
    AudioResponse(uri, file, path, length, addDate) {

    private val imageSize: Size by lazy { ImageUtils.getImageSize(path) }

    open var width: Int = -1
        get() = if (field == -1) imageSize.width.also { field = it } else field

    open var height: Int = -1
        get() = if (field == -1) imageSize.height.also { field = it } else field
}