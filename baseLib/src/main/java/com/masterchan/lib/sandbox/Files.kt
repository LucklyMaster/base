package com.masterchan.lib.sandbox

import android.os.Environment

/**
 * [Environment.DIRECTORY_DOCUMENTS]、[Environment.DIRECTORY_DOWNLOADS]访问实现
 * @author: MasterChan
 * @date: 2022-06-09 16:24
 */
class Files(a: AbsFileAccess) : IFileAccess by a {
}