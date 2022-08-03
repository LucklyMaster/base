package com.master.mybase.activity

import android.os.Bundle
import android.view.View
import com.master.lib.MToast
import com.master.lib.dialog.AlertDialog
import com.master.lib.ext.dp2pxi
import com.master.mybase.R
import com.master.mybase.databinding.ActivityDialogBinding

class DialogActivity : MyBaseActivity<ActivityDialogBinding>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    fun alertDialogClick1(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setPositiveText("确认")
            .create()
            .setWindowColor("#50FFFFFF")
            .setAmount(0F)
            .setContentBlur(dp2pxi(60))
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

    fun alertDialogClick5(view: View) {
        val list = (0..10).map { it.toString() }
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setPositiveText("确认")
            .setSingleChoiceItems(list, 0) { _, _, position ->
                MToast.show(position.toString())
            }
            .create()
            .setOnDismissListener { MToast.show("dismiss") }
            .show()
    }

    fun alertDialogClick6(view: View) {
        val list = (0..10).map { it.toString() }
        AlertDialog.Builder(this)
            .setTitle("Title")
            .setPositiveText("确认")
            .setMultiChoiceItems(list, listOf(0))
            .setOnItemSelectedListener { _, _, checkedItems ->
                MToast.show(checkedItems.toString())
            }
            .create()
            .setWindowBehindBlur(dp2pxi(60))
            .setOnDismissListener { MToast.show("dismiss") }
            .show()
    }
}