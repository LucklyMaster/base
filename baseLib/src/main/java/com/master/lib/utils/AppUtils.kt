package com.master.lib.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import com.master.lib.ext.application
import com.master.lib.ext.topActivity
import java.io.File

/**
 * AppUtils
 * @author: MasterChan
 * @date: 2022-6-26 20:17
 */
object AppUtils {

    @JvmStatic
    @RequiresPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
    fun installApk(filePath: String) {
        installApk(File(filePath))
    }

    @JvmStatic
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

    @JvmStatic
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

    /**
     * 卸载APK，API26以上必须申请[Manifest.permission.REQUEST_DELETE_PACKAGES]权限
     * @param pkgName 包名
     */
    @JvmStatic
    fun uninstallApk(pkgName: String) {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$pkgName")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    /**
     * 根据包名启动一个程序
     * @param packageName 包名
     */
    @JvmStatic
    fun launchApp(packageName: String) {
        val launcherActivity = getLauncherActivity(packageName)
        if (launcherActivity.isEmpty()) return
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setClassName(packageName, launcherActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    /**
     * 获取应用的启动Activity，Android10以上需要[Manifest.permission.QUERY_ALL_PACKAGES]权限或者通过
     * <requires/>显式注明需要查询的包名，否则在无法查询
     * @param packageName 应用包名
     * @return [packageName].launcherActivity
     */
    @JvmStatic
    @SuppressLint("QueryPermissionsNeeded")
    fun getLauncherActivity(packageName: String): String {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(packageName)
        val pm = application.packageManager
        val info = pm.queryIntentActivities(intent, 0)
        return if (info.size == 0) "" else info[0].activityInfo.name
    }

    @JvmStatic
    fun shareText(content: String?, title: String? = null) {
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent = Intent.createChooser(intent, title)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    @JvmStatic
    fun shareImage(vararg uris: Uri) {
        shareImage(null, null, ArrayList(uris.toList()))
    }

    @JvmStatic
    fun shareImage(content: String?, title: String?, vararg uris: Uri) {
        shareImage(content, title, ArrayList(uris.toList()))
    }

    @JvmStatic
    fun shareImage(content: String?, title: String?, uris: ArrayList<Uri>?) {
        var intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        intent.type = "image/*"
        intent = Intent.createChooser(intent, title)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }

    /**
     * 应用是否安装，使用此方法需要[Manifest.permission.QUERY_ALL_PACKAGES]或在[AndroidManifest]中
     * 添加<queries>标签
     * @param packageName String
     * @return Boolean
     */
    fun isInstall(packageName: String): Boolean {
        return application.packageManager.getPackageInfo(packageName, 0) != null
    }
}