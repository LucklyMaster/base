package com.master.lib.permission

import androidx.lifecycle.ViewModel

/**
 * RequestModel
 * @author: MasterChan
 * @date: 2022-07-24 20:24
 */
class RequestModel : ViewModel() {

    var permissionsResultCallback: PermissionsResultCallback? = null
    val interceptorMap = mutableMapOf<String, SpecialPermissionInterceptor>()
    val permissions by lazy { mutableListOf<String>() }

}