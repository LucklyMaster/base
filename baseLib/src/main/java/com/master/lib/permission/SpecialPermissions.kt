package com.master.lib.permission

import android.Manifest

/**
 * 定义框架支持申请的特殊权限，需要特殊处理的权限需要在[IPermission]的对应实现方法中
 * 回调相应的结果
 * @author: MasterChan
 * @date: 2022-07-27 14:37
 */
object SpecialPermissions {
    const val MANAGE_EXTERNAL_STORAGE = Manifest.permission.MANAGE_EXTERNAL_STORAGE
    const val REQUEST_INSTALL_PACKAGES = Manifest.permission.REQUEST_INSTALL_PACKAGES
    const val SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW
    const val WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS
    val list: List<String>
        get() {
            return listOf(
                MANAGE_EXTERNAL_STORAGE, REQUEST_INSTALL_PACKAGES, SYSTEM_ALERT_WINDOW,
                WRITE_SETTINGS
            )
        }
}