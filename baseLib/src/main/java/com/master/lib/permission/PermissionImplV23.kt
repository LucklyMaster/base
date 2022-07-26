package com.master.lib.permission

import android.Manifest
import android.content.Context
import com.master.lib.utils.AndroidVersion

open class PermissionImplV23 : AbsPermission() {

    override fun isGranted(context: Context, permission: String): Boolean {
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
}