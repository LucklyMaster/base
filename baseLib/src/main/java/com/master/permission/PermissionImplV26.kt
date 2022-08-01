package com.master.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.master.utils.AndroidVersion

@RequiresApi(AndroidVersion.ANDROID_8)
open class PermissionImplV26 : PermissionImplV23() {

    override fun isGranted(context: Context, permission: String): Boolean {
        return when (permission) {
            Manifest.permission.READ_PHONE_NUMBERS -> {
                context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                context.packageManager.canRequestPackageInstalls()
            }
            else -> {
                super.isGranted(context, permission)
            }
        }
    }

    override fun getAppDetailIntent(context: Context, permission: String): Intent {
        val intent = super.getAppDetailIntent(context, permission)
        if (SpecialPermissions.REQUEST_INSTALL_PACKAGES == permission) {
            intent.action = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
        }
        return intent
    }
}