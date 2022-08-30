package com.master.lib

import android.app.Application
import com.master.lib.log.MLog
import com.master.lib.permission.MPermissions
import com.master.lib.permission.OnDeniedInterceptor
import com.master.lib.sandbox.Delegate
import com.master.lib.sandbox.request.IFileRequest
import com.master.lib.widget.ActivityStack
import com.master.lib.widget.CrashHandler

/**
 * 初始化入口
 * @author: MasterChan
 * @date: 2022-05-26 22:14
 */
class MCLib {

    companion object {
        @JvmStatic
        lateinit var application: Application

        @JvmStatic
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
    inline fun setLog(method: MLog.() -> Unit) = apply {
        method.invoke(MLog)
    }

    /**
     * 设置[CrashHandler]的配置
     * @param method [@kotlin.ExtensionFunctionType] Function1<CrashHandler, Unit>
     * @return MCLib
     */
    inline fun setCrashHandler(method: CrashHandler.() -> Unit) = apply {
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

    /**
     * 设置权限申请失败的拦截器
     * @param isNeedAllGranted Boolean
     * @param onDeniedInterceptor OnDeniedInterceptor
     * @return MCLib
     */
    fun setOnPermissionDenied(isNeedAllGranted: Boolean, onDeniedInterceptor: OnDeniedInterceptor) =
        apply {
            MPermissions.setOnDeniedInterceptor(isNeedAllGranted, onDeniedInterceptor)
        }
}