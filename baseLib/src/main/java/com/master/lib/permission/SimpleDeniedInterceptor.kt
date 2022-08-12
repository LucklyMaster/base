package com.master.lib.permission

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.master.lib.R
import com.master.lib.base.BaseActivity
import com.master.lib.dialog.AlertDialog
import com.master.lib.ext.toast
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 失败处理
 * @author: MasterChan
 * @date: 2022-08-12 14:40
 */
open class SimpleDeniedInterceptor : OnDeniedInterceptor {

    override fun callback(context: Context, response: PermissionResponse) {
        if (context !is BaseActivity) {
            return
        }
        context.lifecycleScope.launch {
            if (response.hasNeverAsk) {
                onNeverAsk(context, response)
            } else {
                onDenied(context, response)
            }
        }
    }

    protected open suspend fun onNeverAsk(activity: BaseActivity, response: PermissionResponse) {
        response.neverAskPermissions.forEach { onNeverAsk(activity, it) }
    }

    protected open suspend fun onNeverAsk(activity: BaseActivity, permission: String) =
        suspendCoroutine {
            AlertDialog.Builder(activity)
                .setMessage("拒绝权限后将无法使用该功能，请在设置中开启")
                .setNegativeText(R.string.mc_cancel)
                .setPositiveText(R.string.mc_sure)
                .setOnNegativeClickListener { dialog ->
                    dialog.dismiss()
                    it.resume(Unit)
                }
                .setOnPositiveClickListener { dialog ->
                    dialog.dismiss()
                    activity.startActivityForResult(
                        MPermissions.getAppDetailIntent(permission)
                    ) {
                        it.resume(Unit)
                    }
                }
                .create()
                .setCanceledOnTouchOutside(false)
                .show()
        }

    protected open suspend fun onDenied(context: Context, response: PermissionResponse) {
        toast("拒绝权限后将无法使用该功能")
    }
}