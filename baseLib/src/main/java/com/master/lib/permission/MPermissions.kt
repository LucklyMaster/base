package com.master.lib.permission

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

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

    companion object {
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

    fun request(callback: Callback? = null) {
        RequestFragment.request(activity, permissions, callback)
    }
}