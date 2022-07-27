package com.master.lib.permission

/**
 * 特殊权限请求前的拦截器，可以在特殊权限请求前执行自己的逻辑
 * @author: MasterChan
 * @date: 2022-07-27 15:02
 */
fun interface SpecialPermissionInterceptor {

    fun interface OnResultCallback {
        fun onResult(boolean: Boolean)
    }

    fun onIntercept(onResult: OnResultCallback)
}