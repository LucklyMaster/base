package com.masterchan.lib.sandbox

import com.masterchan.lib.ext.isScopedStorage
import com.masterchan.lib.sandbox.request.FileRequest
import com.masterchan.lib.sandbox.request.FileRequestApi28Impl
import com.masterchan.lib.sandbox.request.IFileRequest

/**
 *
 * @author: MasterChan
 * @date: 2022-06-13 23:45
 */
class A {
    fun dy(): IFileRequest {
        return if (isScopedStorage) {
            FileRequest()
        } else {
            FileRequestApi28Impl()
        }
    }
}