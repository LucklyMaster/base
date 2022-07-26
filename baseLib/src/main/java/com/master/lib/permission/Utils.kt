package com.master.lib.permission

import android.Manifest
import android.content.Context
import com.master.lib.permission.Utils.Delegate.getDelegate
import com.master.lib.utils.AndroidVersion


/**
 * 权限处理相关的工具类
 * @author: MasterChan
 * @date: 2022-07-25 16:15
 */
internal object Utils : IPermission by getDelegate() {

    private object Delegate {
        fun getDelegate(): IPermission {
            return if (AndroidVersion.isAndroid12()) {
                PermissionImplV31()
            } else if (AndroidVersion.isAndroid11()) {
                PermissionImplV30()
            } else if (AndroidVersion.isAndroid10()) {
                PermissionImplV29()
            } else if (AndroidVersion.isAndroid8()) {
                PermissionImplV26()
            } else {
                PermissionImplV23()
            }
        }
    }

    /**
     * 将需要申请的权限转换为运行的Android版本的权限
     * @param permissions List<String>
     * @return Map<String, Boolean> value为false表示不是当前版本权限
     */
    fun convertPermissions2CurVersion(permissions: List<String>): Map<String, Boolean> {
        val map = permissions.associateWith { true }.toMutableMap()
        //Android12新增3个蓝牙权限
        if (!AndroidVersion.isAndroid12()) {
            //用于扫描附件其他的蓝牙设备
            if (map.contains(Manifest.permission.BLUETOOTH_SCAN)) {
                map[Manifest.permission.BLUETOOTH_SCAN] = false
                //Android12以下蓝牙扫描需要精确定位权限
                map[Manifest.permission.BLUETOOTH_ADMIN] = true
                map[Manifest.permission.ACCESS_FINE_LOCATION] = true
            }
            // 用于连接之前已经配对过的蓝牙设备
            if (map.contains(Manifest.permission.BLUETOOTH_CONNECT)) {
                map[Manifest.permission.BLUETOOTH_CONNECT] = false
                map[Manifest.permission.BLUETOOTH] = true
            }
            //用于允许当前的设备被其他的蓝牙设备所发现
            if (map.contains(Manifest.permission.BLUETOOTH_ADVERTISE)) {
                map[Manifest.permission.BLUETOOTH_ADVERTISE] = false
                map[Manifest.permission.BLUETOOTH_ADMIN] = true
            }
        }
        //Android11新增存储为特殊权限，需要跳转到权限管理申请
        if (!AndroidVersion.isAndroid11()) {
            //Android11之前不需要MANAGE_EXTERNAL_STORAGE权限
            if (map.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                map[Manifest.permission.MANAGE_EXTERNAL_STORAGE] = false
                map[Manifest.permission.WRITE_EXTERNAL_STORAGE] = true
                map[Manifest.permission.READ_EXTERNAL_STORAGE] = true
            }
        }
        //Android10 ACTIVITY_RECOGNITION权限从传感器权限中剥离
        if (!AndroidVersion.isAndroid10()) {
            if (map.contains(Manifest.permission.ACTIVITY_RECOGNITION)) {
                map[Manifest.permission.ACTIVITY_RECOGNITION] = false
                map[Manifest.permission.BODY_SENSORS] = true
            }
        }
        //Android8新增电话权限
        if (!AndroidVersion.isAndroid8()) {
            if (map.contains(Manifest.permission.READ_PHONE_NUMBERS)) {
                map[Manifest.permission.READ_PHONE_NUMBERS] = false
                map[Manifest.permission.READ_PHONE_STATE] = true
            }
        }
        return map
    }
}