package com.masterchan.lib

import android.app.Application
import com.masterchan.lib.log.MLog
import com.masterchan.lib.sandbox.Delegate
import com.masterchan.lib.sandbox.request.IFileRequest

/**
 * 初始化入口
 * @author: MasterChan
 * @date: 2022-05-26 22:14
 */
class MCLib {

    companion object {
        lateinit var application: Application

        fun init(app: Application): MCLib {
            application = app
            return MCLib()
        }
    }

    init {
        ActivityStack.instance.init(application)
    }

    /**
     * 设置[MLog]的配置
     * @param method
     * @return MCLib
     */
    fun setLog(method: MLog.() -> Unit) = apply {
        method.invoke(MLog)
    }

    /**
     * 设置[CrashHandler]的配置
     * @param method [@kotlin.ExtensionFunctionType] Function1<CrashHandler, Unit>
     * @return MCLib
     */
    fun setCrashHandler(method: CrashHandler.() -> Unit) = apply {
        method.invoke(CrashHandler.instance)
    }

    /**
     * 设置媒体库访问代理
     * @param delegate Function0<IFileRequest>
     * @return MCLib
     */
    fun setMediaAccessDelegate(delegate: () -> IFileRequest) = apply {
        Delegate.setRequestDelegate(delegate)
    }
}