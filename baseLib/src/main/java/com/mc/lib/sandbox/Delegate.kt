package com.mc.lib.sandbox

import com.mc.lib.ext.isScopedStorage
import com.mc.lib.sandbox.request.FileRequest
import com.mc.lib.sandbox.request.FileRequestApi28Impl
import com.mc.lib.sandbox.request.IFileRequest

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