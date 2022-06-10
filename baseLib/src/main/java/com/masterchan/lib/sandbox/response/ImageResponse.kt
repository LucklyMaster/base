package com.masterchan.lib.sandbox.response

import android.net.Uri
import android.util.Size
import com.masterchan.lib.ext.Log
import com.masterchan.lib.utils.ImageUtils
import java.io.File

/**
 * API29以下图片访问结果
 * @author: MasterChan
 * @date: 2022-06-10 14:57
 */
open class ImageResponse(uri: Uri, file: File, path: String, length: Long) :
    FileResponse(uri, file, path, length) {

    private val imageSize: Size by lazy {
        Log.d("imageSize = $imageSize")
        ImageUtils.getImageSize(path)
    }

    open var width: Int = -1
        get() = if (field == -1) imageSize.width.also { field = it } else field

    open var height: Int = -1
        get() = if (field == -1) imageSize.height.also { field = it } else field
}