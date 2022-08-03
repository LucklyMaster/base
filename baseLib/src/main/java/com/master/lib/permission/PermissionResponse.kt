package com.master.lib.permission

/**
 * 权限申请结果
 * @author: MasterChan
 * @date: 2022-07-24 16:51
 */
data class PermissionResponse(
    /**
     * 请求的全部权限
     */
    val requestPermissions: List<String>,
    /**
     * 授权通过的权限
     */
    val grantedPermissions: List<String>,
    /**
     * 授权不通过的权限
     */
    val deniedPermissions: List<String>,
    /**
     * 选择了不再询问的权限
     */
    val neverAskPermissions: List<String>
) {
    /**
     * 只要有一个权限通过，则返回true
     */
    val isGranted: Boolean
        get() {
            return if (requestPermissions.size == 1) {
                allGranted
            } else {
                allGranted || !allDenied
            }
        }

    /**
     * 全部权限通过返回true
     */
    val allGranted: Boolean
        get() {
            return requestPermissions.size == grantedPermissions.size
        }

    /**
     * 全部权限不通过返回true
     */
    val allDenied: Boolean
        get() {
            return requestPermissions.size == deniedPermissions.size
        }

    /**
     * 请求的权限中是否有权限选择了不再询问
     */
    val hasNeverAsk: Boolean
        get() {
            return neverAskPermissions.isNotEmpty()
        }

    override fun toString(): String {
        return """
            ${javaClass.simpleName}(
                isGranted: $isGranted
                allGranted: $allGranted
                allDenied: $allDenied
                hasNeverAsk: $hasNeverAsk
                requestPermissions: ${
            requestPermissions.toString().replace("android.permission.", "")
        }
                grantedPermissions: ${
            grantedPermissions.toString().replace("android.permission.", "")
        }
                deniedPermissions: ${
            deniedPermissions.toString().replace("android.permission.", "")
        }
                neverAskPermissions: ${
            neverAskPermissions.toString().replace("android.permission.", "")
        }
            )
        """.trimIndent()
    }
}