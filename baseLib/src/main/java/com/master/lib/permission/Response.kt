package com.master.lib.permission

/**
 * 权限申请结果
 * @author: MasterChan
 * @date: 2022-07-24 16:51
 */
data class Response(
    val permissionsDetail: Map<String, Int>,
    val requestPermissions: List<String>,
    val grantedPermissions: List<String>,
    val deniedPermissions: List<String>,
) {
    val isGranted: Boolean
        get() {
            return if (requestPermissions.size == 1) {
                allGranted
            } else {
                !allGranted && !allDenied
            }
        }

    val allGranted: Boolean
        get() {
            return requestPermissions.size == grantedPermissions.size
        }

    val allDenied: Boolean
        get() {
            return requestPermissions.size == deniedPermissions.size
        }

    val hasNever: Boolean
        get() {
            return permissionsDetail.firstNotNullOfOrNull { it.value == State.NEVER } != null
        }
}