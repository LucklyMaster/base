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
                .setMessage(activity.getString(R.string.tip_refused_permission_when_never_ask))
                .setNegativeText(R.string.mc_cancel)
                .setPositiveText(R.string.mc_sure)
                .setOnNegativeClickListener { dialog ->
                    dialog.dismiss()
                    it.resume(Unit)
                }
                .setOnPositiveClickListener { dialog ->
                    dialog.dismiss()
                    val intent = if (SpecialPermissions.list.contains(permission)) {
                        MPermissions.getAppDetailIntent()
                    } else {
                        MPermissions.getPermissionDetailIntent()
                    }
                    activity.startActivityForResult(intent) {
                        it.resume(Unit)
                    }
                }
                .create()
                .setCanceledOnTouchOutside(false)
                .show()
        }

    protected open suspend fun onDenied(context: Context, response: PermissionResponse) {
        toast(context.getString(R.string.tip_refused_permission_when_denied))
    }
}