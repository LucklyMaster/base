package com.master.lib.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.master.lib.ext.activity
import com.master.lib.utils.AndroidVersion

/**
 * 权限接口
 * @author: MasterChan
 * @date: 2022-07-25 22:39
 */
interface IPermission {

    /**
     * 判断是否授予权限
     * @param context Context
     * @param permission String
     * @return Boolean
     */
    fun isGranted(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isAllGranted(context: Context, permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (!Utils.isGranted(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * 判断是否选择不再询问
     * @param context Context
     * @param permission String
     * @return Boolean
     */
    fun isNeverAsk(context: Context, permission: String): Boolean {
        return !isGranted(context, permission) && !shouldShowRequestPermissionRationale(
            context, permission
        )
    }

    fun getAppDetailIntent(context: Context, permission: String): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = getPackageNameUri(context)
        return intent
    }

    fun getPackageNameUri(context: Context): Uri? {
        return Uri.parse("package:" + context.packageName)
    }

    fun shouldShowRequestPermissionRationale(context: Context, permission: String): Boolean {
        val activity = context.activity
        if (activity == null || AndroidVersion.isAndroid12()) {
            try {
                val packageManager = if (activity != null) {
                    activity.application.packageManager
                } else {
                    context.packageManager
                }
                val method = PackageManager::class.java.getMethod(
                    "shouldShowRequestPermissionRationale",
                    String::class.java
                )
                return method.invoke(packageManager, permission) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return activity!!.shouldShowRequestPermissionRationale(permission)
    }
}