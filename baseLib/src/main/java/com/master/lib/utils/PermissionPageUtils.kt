package com.master.lib.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.master.lib.ext.packageName

/**
 * PermissionPageUtils
 * @author: MasterChan
 * @date: 2022-09-02 14:57
 */
object PermissionPageUtils {

    private const val HUAWEI = "HUAWEI"
    private const val MEIZU = "Meizu"
    private const val XIAOMI = "Xiaomi"
    private const val SONY = "Sony"
    private const val OPPO = "OPPO"
    private const val LG = "LG"
    private const val VIVO = "vivo"

    fun getPermissionIntent(context: Context): Intent {
        return when (Build.MANUFACTURER) {
            HUAWEI -> huawei(context)
            MEIZU -> meizu(context)
            XIAOMI -> xiaomi(context)
            SONY -> sony(context)
            OPPO -> oppo(context)
            VIVO -> vivo(context)
            LG -> lg(context)
            else -> {
                defaultIntent()
            }
        }
    }

    fun startPermissionIntent(context: Context) {
        try {
            context.startActivity(getPermissionIntent(context))
        } catch (e: Exception) {
            e.printStackTrace()
            context.startActivity(defaultIntent())
        }
    }

    private fun huawei(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        intent.component = ComponentName(
            "com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"
        )
        return intent
    }

    private fun meizu(context: Context): Intent {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("packageName", packageName)
        return intent
    }

    private fun xiaomi(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname", packageName)
        val componentName = ComponentName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.component = componentName
        return intent
    }

    private fun sony(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        intent.component = ComponentName(
            "com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity"
        )
        return intent
    }

    private fun oppo(context: Context): Intent {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        val comp = ComponentName(
            "com.coloros.securitypermission",
            "com.coloros.securitypermission.permission.PermissionAppAllPermissionActivity"
        )
        intent.component = comp
        return intent
    }

    private fun vivo(context: Context): Intent {
        val intent: Intent
        if (Build.MODEL.contains("Y85") && !Build.MODEL.contains("Y85A") ||
            Build.MODEL.contains("vivo Y53L")
        ) {
            intent = Intent()
            intent.setClassName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.context.PurviewTabActivity"
            )
            intent.putExtra("packagename", packageName)
            intent.putExtra("tabId", "1")
        } else {
            intent = Intent()
            intent.setClassName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.context.SoftPermissionDetailActivity"
            )
            intent.action = "secure.intent.action.softPermissionDetail"
            intent.putExtra("packagename", packageName)
        }
        return intent
    }

    private fun lg(context: Context): Intent {
        val intent = Intent("android.intent.action.MAIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", packageName)
        intent.component = ComponentName(
            "com.android.settings", "com.android.settings.Settings\$AccessLockSummaryActivity"
        )
        return intent
    }

    /**
     * 应用信息界面
     */
    private fun defaultIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        return intent
    }
}