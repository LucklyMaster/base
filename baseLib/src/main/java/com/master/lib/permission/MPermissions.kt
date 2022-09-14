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

    private var check = false

    /**
     * 特殊权限拦截器
     */
    private val specialInterceptors = mutableMapOf<String, SpecialPermissionInterceptor>()

    /**
     * 是否将所有权限全部成功授权判断为[PermissionResponse.isGranted]，只对失败拦截器生效
     */
    private var isNeedAllGranted = false

    /**
     * 申请权限失败后的拦截器
     */
    private var onDeniedInterceptor: OnDeniedInterceptor? = null

    companion object Static {
        private var isNeedAllGranted = false
        private var onDeniedInterceptor: OnDeniedInterceptor? = null

        @JvmStatic
        fun with(context: Context): MPermissions {
            require(context is FragmentActivity) { "the context must be a FragmentActivity" }
            return MPermissions(context).apply {
                isNeedAllGranted(Static.isNeedAllGranted)
                setOnDeniedInterceptor(Static.onDeniedInterceptor)
            }
        }

        @JvmStatic
        fun with(activity: FragmentActivity): MPermissions {
            return MPermissions(activity).apply {
                isNeedAllGranted(Static.isNeedAllGranted)
                setOnDeniedInterceptor(Static.onDeniedInterceptor)
            }
        }

        @JvmStatic
        fun with(fragment: Fragment): MPermissions {
            return MPermissions(fragment.requireActivity())
        }

        /**
         * 设置申请权限失败后的拦截器，设置后[request]将不再返回失败回调；
         * 所有申请失败的权限将从此拦截器回调
         * @param interceptor OnDeniedInterceptor
         * @return MPermissions
         */
        @JvmStatic
        fun setOnDeniedInterceptor(
            isNeedAllGranted: Boolean,
            interceptor: OnDeniedInterceptor?
        ) = apply {
            this.isNeedAllGranted = isNeedAllGranted
            this.onDeniedInterceptor = interceptor
        }

        @JvmStatic
        fun isGranted(permission: String): Boolean {
            return PermissionsUtils.isGranted(application, permission)
        }

        @JvmStatic
        fun isAllGranted(permissions: List<String>): Boolean {
            return PermissionsUtils.isAllGranted(application, permissions)
        }

        @JvmStatic
        fun isNeverAsk(permission: String): Boolean {
            return PermissionsUtils.isNeverAsk(application, permission)
        }

        @JvmStatic
        fun getAppDetailIntent(): Intent {
            return PermissionsUtils.getAppDetailIntent(application, "")
        }

        @JvmStatic
        fun getPermissionDetailIntent(): Intent {
            return PermissionsUtils.getPermissionDetailIntent(application, "")
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

    fun check(check: Boolean) = apply {
        this.check = check
    }

    /**
     * 是否所有权限全部授权判断为已授权
     * @param isNeedAllGranted Boolean
     * @return MPermissions
     */
    fun isNeedAllGranted(isNeedAllGranted: Boolean) = apply {
        this.isNeedAllGranted = isNeedAllGranted
    }

    /**
     * 设置失败拦截器，将会覆盖全局失败拦截器，如果设置了[Static.setOnDeniedInterceptor]后，设置null，
     * 所有结果的回调将会走向[request]
     * @param interceptor OnDeniedInterceptor?
     * @return MPermissions
     */
    fun setOnDeniedInterceptor(interceptor: OnDeniedInterceptor?) = apply {
        this.onDeniedInterceptor = interceptor
    }

    /**
     * 为特殊权限的申请添加一个拦截器，可以在申请权限之前执行一些业务逻辑
     * @param permission String
     * @param interceptor SpecialPermissionInterceptor
     * @return MPermissions
     */
    fun addSpecialPermissionInterceptor(
        permission: String,
        interceptor: SpecialPermissionInterceptor
    ) = apply {
        specialInterceptors[permission] = interceptor
    }

    fun request(callback: OnResultCallback) {
        RequestFragment.request(
            activity, permissions, check, callback, isNeedAllGranted, onDeniedInterceptor,
            specialInterceptors
        )
    }
}