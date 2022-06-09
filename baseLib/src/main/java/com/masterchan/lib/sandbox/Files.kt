package com.masterchan.lib.sandbox

import android.os.Environment
import com.masterchan.lib.ext.scopedStorage

/**
 * [Environment.DIRECTORY_DOCUMENTS]、[Environment.DIRECTORY_DOWNLOADS]访问实现
 * @author: MasterChan
 * @date: 2022-06-09 16:24
 */
class Files : AbsFileAccess() {

    override fun listFile(publicDir: String?): AbsFileBean? {
        checkPublicDir(publicDir)
        if (scopedStorage) {
            FileAccess29Impl().listFile(publicDir)
        }
        return null
    }
}