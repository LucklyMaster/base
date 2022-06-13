package com.masterchan.lib.sandbox

import android.os.Environment
import com.masterchan.lib.ext.isScopedStorage
import com.masterchan.lib.sandbox.request.FileRequest
import com.masterchan.lib.sandbox.request.FileRequestApi28Impl
import com.masterchan.lib.sandbox.request.IFileRequest

/**
 * [Environment.DIRECTORY_DOCUMENTS]、[Environment.DIRECTORY_DOWNLOADS]访问实现
 * @author: MasterChan
 * @date: 2022-06-09 16:24
 */
object FileAccess : IFileRequest by A().dy() {
}