package com.master.lib.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

abstract class AbsPermission : IPermission {

    override fun isGranted(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun isNeverAsk(context: Context, permission: String): Boolean {
        return !isGranted(context, permission) && !shouldShowRequestPermissionRationale(
            context, permission
        )
    }

    override fun getAppDetailIntent(context: Context, permission: String): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = getPackageNameUri(context)
        return intent
    }

    protected fun getPackageNameUri(context: Context): Uri? {
        return Uri.parse("package:" + context.packageName)
    }
}