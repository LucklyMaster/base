package com.master.lib.permission

import android.content.Context
import android.content.Intent

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
    fun isGranted(context: Context, permission: String): Boolean

    /**
     * 判断是否拒绝权限
     * @param context Context
     * @param permission String
     * @return Boolean
     */
    fun isDenied(context: Context, permission: String): Boolean

    fun getAppDetailIntent(context: Context, permission: String): Intent
}