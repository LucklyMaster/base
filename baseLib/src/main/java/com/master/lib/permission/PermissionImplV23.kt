package com.master.lib.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.master.lib.utils.AndroidVersion

open class PermissionImplV23 : IPermission {

    override fun isGranted(context: Context, permission: String): Boolean {
        //特殊权限
        if (SpecialPermissions.list.contains(permission)) {
            //悬浮窗权限
            if (SpecialPermissions.SYSTEM_ALERT_WINDOW == permission) {
                return Settings.canDrawOverlays(context)
            }
            // 系统设置权限
            if (SpecialPermissions.WRITE_SETTINGS == permission) {
                return if (AndroidVersion.isAndroid6()) {
                    Settings.System.canWrite(context)
                } else {
                    true
                }
            }
        }
        //Android12蓝牙权限
        if (!AndroidVersion.isAndroid12()) {
            if (Manifest.permission.BLUETOOTH_SCAN == permission) {
                return super.isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (Manifest.permission.BLUETOOTH_CONNECT == permission ||
                Manifest.permission.BLUETOOTH_ADVERTISE == permission
            ) {
                return true
            }
        }
        //Android10文件管理权限
        if (Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission) {
            return super.isGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        //Android8电话权限
        if (Manifest.permission.READ_PHONE_NUMBERS == permission) {
            return super.isGranted(context, Manifest.permission.READ_PHONE_STATE)
        }
        return super.isGranted(context, permission)
    }

    override fun isNeverAsk(context: Context, permission: String): Boolean {
        //特殊权限直接返回false
        if (SpecialPermissions.list.contains(permission)) {
            return false
        }
        //判断危险权限
        if (!AndroidVersion.isAndroid12()) {
            if (Manifest.permission.BLUETOOTH_SCAN == permission) {
                return super.isNeverAsk(context, Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (Manifest.permission.BLUETOOTH_CONNECT == permission ||
                Manifest.permission.BLUETOOTH_ADVERTISE == permission
            ) {
                return false
            }
        }
        if (Manifest.permission.MANAGE_EXTERNAL_STORAGE == permission) {
            return super.isNeverAsk(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!AndroidVersion.isAndroid10()) {
            if (Manifest.permission.ACTIVITY_RECOGNITION == permission) {
                return super.isNeverAsk(context, Manifest.permission.BODY_SENSORS)
            }
        }
        if (!AndroidVersion.isAndroid8()) {
            if (Manifest.permission.READ_PHONE_NUMBERS == permission) {
                return super.isNeverAsk(context, Manifest.permission.READ_PHONE_STATE)
            }
        }
        return super.isNeverAsk(context, permission)
    }

    override fun getAppDetailIntent(context: Context, permission: String): Intent {
        val intent = super.getAppDetailIntent(context, permission)
        intent.action = when (permission) {
            SpecialPermissions.SYSTEM_ALERT_WINDOW -> Settings.ACTION_MANAGE_OVERLAY_PERMISSION
            SpecialPermissions.WRITE_SETTINGS -> Settings.ACTION_MANAGE_WRITE_SETTINGS
            else -> intent.action
        }
        return intent
    }
}