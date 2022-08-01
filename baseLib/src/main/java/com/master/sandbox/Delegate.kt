package com.master.sandbox

import com.master.ext.isScopedStorage
import com.master.sandbox.request.FileRequest
import com.master.sandbox.request.FileRequestApi28Impl
import com.master.sandbox.request.IFileRequest

/**
 * 媒体库访问的代理实现
 * @author: MasterChan
 * @date: 2022-06-13 23:45
 */
object Delegate {

    private var delegate: (() -> IFileRequest)? = null

    fun get(): IFileRequest {
        if (delegate != null) {
            return delegate!!.invoke()
        }
        return if (isScopedStorage) {
            FileRequest()
        } else {
            FileRequestApi28Impl()
        }
    }

    fun setRequestDelegate(request: () -> IFileRequest) {
        delegate = request
    }
}