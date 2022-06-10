package com.masterchan.lib.sandbox

import android.os.Environment
import com.masterchan.lib.sandbox.access.AbsFileRequest
import com.masterchan.lib.sandbox.access.IFileRequest

/**
 * [Environment.DIRECTORY_DOCUMENTS]、[Environment.DIRECTORY_DOWNLOADS]访问实现
 * @author: MasterChan
 * @date: 2022-06-09 16:24
 */
class Files(a: AbsFileRequest) : IFileRequest by a {
}