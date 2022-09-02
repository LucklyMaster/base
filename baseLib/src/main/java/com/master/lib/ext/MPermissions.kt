package com.master.lib.ext

import android.content.Context
import androidx.fragment.app.Fragment
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
        .isNeedAllGranted(isNeedAllGranted).apply {
            deniedCallback?.let { setOnDeniedInterceptor(it) }
        }
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

fun Fragment.requestPermissions(
    permissions: Array<String>,
    isNeedAllGranted: Boolean,
    deniedCallback: OnDeniedInterceptor?,
    grantedCallback: OnResultCallback
) {
    requireActivity().requestPermissions(
        permissions, isNeedAllGranted, deniedCallback, grantedCallback
    )
}

fun Fragment.requestPermissions(
    permissions: Array<String>,
    deniedCallback: OnDeniedInterceptor?,
    grantedCallback: OnResultCallback
) {
    requireActivity().requestPermissions(permissions, true, deniedCallback, grantedCallback)
}

fun Fragment.requestPermissions(vararg permissions: String, grantedCallback: OnResultCallback) {
    requireActivity().requestPermissions(arrayOf(*permissions), true, null, grantedCallback)
}