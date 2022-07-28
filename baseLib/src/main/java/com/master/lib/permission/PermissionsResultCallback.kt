package com.master.lib.permission

/**
 * 权限申请结果回调
 * @author: MasterChan
 * @date: 2022-07-24 16:50
 */
fun interface PermissionsResultCallback {
    fun callback(response: PermissionResponse)
}