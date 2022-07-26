package com.master.lib.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

open class PermissionImplV26 : PermissionImplV23() {

    override fun isGranted(context: Context, permission: String): Boolean {
        if (Manifest.permission.READ_PHONE_NUMBERS == permission) {
            return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        return super.isGranted(context, permission)
    }

}