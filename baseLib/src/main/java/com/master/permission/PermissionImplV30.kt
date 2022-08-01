package com.master.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.master.utils.AndroidVersion

@RequiresApi(AndroidVersion.ANDROID_11)
open class PermissionImplV30 : PermissionImplV29() {

    override fun isGranted(context: Context, permission: String): Boolean {
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
        val intent = super.getAppDetailIntent(context, permission)
        if (SpecialPermissions.MANAGE_EXTERNAL_STORAGE == permission) {
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        }
        return intent
    }
}