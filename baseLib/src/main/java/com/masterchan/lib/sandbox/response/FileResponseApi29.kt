package com.masterchan.lib.sandbox.response

import android.net.Uri
import java.io.File

/**
 * API29以上的访问结果
 * @author: MasterChan
 * @date: 2022-06-09 14:50
 */
class FileResponseApi29(uri: Uri, file: File, path: String, override var addDate: Long, size: Long) :
    FileResponse(uri, file, path, size)