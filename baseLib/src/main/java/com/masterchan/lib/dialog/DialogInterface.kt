package com.masterchan.lib.dialog

import android.app.Dialog
import android.widget.Button

/**
 * DialogInterface
 * @author MasterChan
 * @date 2021-12-15 09:59
 */
interface DialogInterface {

    fun interface OnClickListener {
        fun onClick(dialog: Dialog, button: Button)
    }

    fun interface OnDismissListener {
        fun onDismiss(dialog: Dialog)
    }

    fun interface OnCancelListener {
        fun onCancel(dialog: Dialog)
    }
}