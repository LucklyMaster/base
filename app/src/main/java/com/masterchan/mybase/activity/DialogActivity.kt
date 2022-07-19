package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import com.master.lib.MToast
import com.master.lib.dialog.AlertDialog
import com.master.lib.ext.dp2pxi
import com.masterchan.mybase.R
import com.masterchan.mybase.databinding.ActivityDialogBinding

class DialogActivity : MyBaseActivity<ActivityDialogBinding>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    fun alertDialogClick1(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setPositiveText("确认")
            .create()
            .setOnDismissListener { MToast.show("dismiss") }
            .show()
    }

    fun alertDialogClick2(view: View) {
        AlertDialog.Builder(this)
            .setMessage("Message")
            .setPositiveText("确认")
            .create()
            .setOnDismissListener { MToast.show("dismiss") }
            .show()
    }

    fun alertDialogClick3(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setMessage("Message")
            .setPositiveText("确认")
            .create()
            .setOnDismissListener { MToast.show("dismiss") }
            .show()
    }

    fun alertDialogClick4(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setMessage(R.string.dialog_test)
            .setPositiveText("确认")
            .create()
            .setHeight(dp2pxi(300))
            .setOnDismissListener { MToast.show("dismiss") }
            .show()
    }
}