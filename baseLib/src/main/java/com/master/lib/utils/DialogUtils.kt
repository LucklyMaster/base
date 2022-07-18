package com.master.lib.utils

import androidx.fragment.app.FragmentActivity
import com.master.lib.dialog.BaseDialog

/**
 * DialogUtils
 * @author: MasterChan
 * @date: 2022-07-17 23:34
 */
object DialogUtils {

    fun dismissAll(activity: FragmentActivity) {
        activity.supportFragmentManager.fragments.forEach {
            if (it is BaseDialog) {
                it.dismiss()
            }
        }
    }

    fun dismiss(activity: FragmentActivity, tag: String?) {
        activity.supportFragmentManager.fragments.forEach {
            if (it is BaseDialog && it.tag.equals(tag)) {
                it.dismiss()
            }
        }
    }
}