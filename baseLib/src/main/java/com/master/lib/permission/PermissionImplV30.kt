package com.master.lib.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.master.lib.ext.isScopedStorage

@RequiresApi(Build.VERSION_CODES.R)
open class PermissionImplV30 : PermissionImplV29() {

    override fun isGranted(context: Context, permission: String): Boolean {
        if (isScopedStorage) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE == permission ||
                Manifest.permission.READ_EXTERNAL_STORAGE == permission
            ) {
                return true
            }
        }
        if (Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission) {
            return Environment.isExternalStorageManager()
        }
        return super.isGranted(context, permission)
    }

    override fun isNeverAsk(context: Context, permission: String): Boolean {
        if (Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission) {
            return false
        }
        return super.isNeverAsk(context, permission)
    }

    override fun getAppDetailIntent(context: Context, permission: String): Intent {
        if (Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission) {

        }
        return super.getAppDetailIntent(context, permission)
    }
}