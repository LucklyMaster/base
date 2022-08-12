package com.master.lib.permission

import android.content.Context

/**
 * 权限申请失败拦截器
 * @author: MasterChan
 * @date: 2022-08-12 14:43
 */
interface OnDeniedInterceptor {

    fun callback(context: Context, response: PermissionResponse)

}