package com.masterchan.lib.sandbox.response

import android.net.Uri
import java.io.File

/**
 * API29以上音频访问结果
 * @author: MasterChan
 * @date: 2022-06-10 15:30
 */
class AudioResponseApi29(
    uri: Uri,
    file: File,
    path: String,
    length: Long,
    override var addDate: Long,
    override var duration: Long
) : AudioResponse(uri, file, path, length) {
}