package com.master.lib.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import com.master.lib.ext.application
import com.master.lib.ext.topActivity
import java.io.File

object IntentUtils {

    /**
     * 获取应用的启动Activity
     * @param packageName 应用包名
     * @return [packageName].launcherActivity
     */
    @SuppressLint("QueryPermissionsNeeded")
    fun getLauncherActivity(packageName: String): String {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(packageName)
        val pm = application.packageManager
        val info = pm.queryIntentActivities(intent, 0)
        return if (info.size == 0) "" else info[0].activityInfo.name
    }

    @RequiresPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
    fun installApk(filePath: String) {
        installApk(File(filePath))
    }

    @RequiresPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
    fun installApk(file: File) {
        if (!file.exists()) {
            return
        }
        val uri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(file)
        } else {
            val authority = "${application.packageName}.fileprovider"
            FileProvider.getUriForFile(application, authority, file)
        }
        return installApk(uri)
    }

    @RequiresPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
    fun installApk(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        topActivity?.startActivity(intent)
    }
}