package com.master.lib.dialog

import android.app.Dialog
import android.widget.Button

/**
 * DialogInterface
 * @author MasterChan
 * @date 2021-12-15 09:59
 */
interface DialogInterface {

    fun interface OnClickListener {
        fun onClick(dialog: BaseDialog, button: Button)
    }

    fun interface OnShowListener {
        fun onShow(dialog: BaseDialog)
    }

    fun interface OnDismissListener {
        fun onDismiss(dialog: BaseDialog)
    }

    fun interface OnCancelListener {
        fun onCancel(dialog: BaseDialog)
    }
}