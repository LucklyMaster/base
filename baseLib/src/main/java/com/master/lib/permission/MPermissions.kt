package com.master.lib.permission

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.master.lib.ext.application

/**
 * MPermissions
 * @author: MasterChan
 * @date: 2022-07-24 15:42
 */
class MPermissions private constructor(private val activity: FragmentActivity) {

    /**
     * 需要申请的权限
     */
    private val permissions: MutableList<String> by lazy { mutableListOf() }
    private val interceptorMap = mutableMapOf<String, SpecialPermissionInterceptor>()

    companion object Static {
        fun with(context: Context): MPermissions {
            require(context is FragmentActivity) { "the context must be a FragmentActivity" }
            return MPermissions(context)
        }

        fun with(activity: FragmentActivity): MPermissions {
            return MPermissions(activity)
        }

        fun with(fragment: Fragment): MPermissions {
            return MPermissions(fragment.requireActivity())
        }

        fun isGranted(permission: String): Boolean {
            return Utils.isGranted(application, permission)
        }

        fun isAllGranted(permissions: List<String>): Boolean {
            return Utils.isAllGranted(application, permissions)
        }

        fun isNeverAsk(permission: String): Boolean {
            return Utils.isNeverAsk(application, permission)
        }

        fun getAppDetailIntent(permission: String = ""): Intent {
            return Utils.getAppDetailIntent(application, permission)
        }
    }

    fun permissions(vararg permissions: String) = apply {
        permissions(permissions.toList())
    }

    fun permissions(permissions: List<String>) = apply {
        permissions.forEach {
            if (!this.permissions.contains(it)) {
                this.permissions.add(it)
            }
        }
    }

    fun addSpecialPermissionInterceptor(
        permission: String,
        interceptor: SpecialPermissionInterceptor
    ) = apply {
        interceptorMap[permission] = interceptor
    }

    fun request(callback: PermissionsResultCallback? = null) {
        RequestFragment.request(activity, permissions, callback, interceptorMap)
    }
}