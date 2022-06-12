package com.masterchan.lib.sandbox

import android.os.Environment
import com.masterchan.lib.sandbox.request.IFileRequest

/**
 * [Environment.DIRECTORY_DOCUMENTS]、[Environment.DIRECTORY_DOWNLOADS]访问实现
 * @author: MasterChan
 * @date: 2022-06-09 16:24
 */
class FileAccess(request: IFileRequest) : IFileRequest by request