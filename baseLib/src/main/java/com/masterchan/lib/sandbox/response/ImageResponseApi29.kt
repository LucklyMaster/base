package com.masterchan.lib.sandbox.response

import android.net.Uri
import java.io.File

/**
 * API29以上图片访问结果
 * @author: MasterChan
 * @date: 2022-06-10 14:57
 */
class ImageResponseApi29(
    uri: Uri,
    file: File,
    path: String,
    length: Long,
    override var addDate: Long,
    override var width: Int,
    override var height: Int
) :
    ImageResponse(uri, file, path, length)