package com.master.lib.ext

import android.content.Context
import com.master.lib.permission.MPermissions
import com.master.lib.permission.OnDeniedInterceptor
import com.master.lib.permission.OnResultCallback

fun Context.requestPermissions(
    permissions: Array<String>,
    isNeedAllGranted: Boolean,
    deniedCallback: OnDeniedInterceptor?,
    grantedCallback: OnResultCallback
) {
    MPermissions.with(this)
        .permissions(*permissions)
        .isNeedAllGranted(isNeedAllGranted)
        .setOnDeniedInterceptor(deniedCallback)
        .request(grantedCallback)
}

fun Context.requestPermissions(
    permissions: Array<String>,
    deniedCallback: OnDeniedInterceptor?,
    grantedCallback: OnResultCallback
) {
    requestPermissions(permissions, true, deniedCallback, grantedCallback)
}

fun Context.requestPermissions(
    vararg permissions: String,
    grantedCallback: OnResultCallback
) {
    requestPermissions(arrayOf(*permissions), true, null, grantedCallback)
}