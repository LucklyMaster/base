package com.master.lib.permission

import android.Manifest
import com.master.lib.ext.application
import com.master.lib.utils.AndroidVersion
import com.master.lib.utils.XmlUtils

/**
 * 权限处理相关的工具类
 * @author: MasterChan
 * @date: 2022-07-25 16:15
 */
internal object Utils {

    fun transformPermissions(permissions: MutableList<String>): MutableList<String> {
        val manifestPermissions = XmlUtils.getManifestPermissions(application)
        val list = permissions.toMutableSet()
        //Android12新增3个蓝牙权限
        if (!AndroidVersion.isAndroid12()) {
            //用于扫描附件其他的蓝牙设备
            if (list.contains(Manifest.permission.BLUETOOTH_SCAN)) {
                list.remove(Manifest.permission.BLUETOOTH_SCAN)
            }
            // 用于连接之前已经配对过的蓝牙设备
            if (list.contains(Manifest.permission.BLUETOOTH_CONNECT)) {
                list.remove(Manifest.permission.BLUETOOTH_CONNECT)
            }
            //用于允许当前的设备被其他的蓝牙设备所发现
            if (list.contains(Manifest.permission.BLUETOOTH_ADVERTISE)) {
                list.remove(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
        }
        //Android11新增存储为特殊权限，需要跳转到权限管理申请
        if (!AndroidVersion.isAndroid11()) {
            //Android11之前不需要MANAGE_EXTERNAL_STORAGE权限
            if (list.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                list.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                list.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                //Android11之后不需要WRITE_EXTERNAL_STORAGE、READ_EXTERNAL_STORAGE权限
                list.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                list.remove(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        //Android10 ACTIVITY_RECOGNITION权限从传感器权限中剥离
        if (!AndroidVersion.isAndroid10()) {
            if (list.contains(Manifest.permission.ACTIVITY_RECOGNITION)) {
                list.add(Manifest.permission.BODY_SENSORS)
            }
        }
        //Android8新增电话权限
        if (!AndroidVersion.isAndroid8()) {
            if (list.contains(Manifest.permission.READ_PHONE_NUMBERS)) {
                list.remove(Manifest.permission.READ_PHONE_NUMBERS)
                list.add(Manifest.permission.READ_PHONE_STATE)
            }
        }
        return list.toMutableList()
    }
}