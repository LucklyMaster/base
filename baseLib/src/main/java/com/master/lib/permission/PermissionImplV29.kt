package com.master.lib.permission

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresApi
import com.master.lib.ext.isScopedStorage
import com.master.lib.utils.AndroidVersion

@RequiresApi(AndroidVersion.ANDROID_10)
open class PermissionImplV29 : PermissionImplV26() {

    override fun isGranted(context: Context, permission: String): Boolean {
        if (isScopedStorage) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE == permission ||
                Manifest.permission.READ_EXTERNAL_STORAGE == permission
            ) {
                return true
            }
        }
        return super.isGranted(context, permission)
    }

}