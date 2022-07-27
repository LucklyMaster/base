package com.master.lib.permission

import androidx.lifecycle.ViewModel

/**
 * RequestModel
 * @author: MasterChan
 * @date: 2022-07-24 20:24
 */
class RequestModel : ViewModel() {

    var callback: PermissionsResultCallback? = null
    val interceptorMap = mutableMapOf<String, SpecialPermissionInterceptor>()
    val permissions by lazy { mutableListOf<String>() }

    fun onRequestPermissionsResultCallback(result: Response) {
        callback?.callback(result)
    }
}