package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.master.lib.dialog.BaseDialog
import com.masterchan.mybase.databinding.ActivityDialogBinding

class DialogActivity : MyBaseActivity<ActivityDialogBinding>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    fun baseDialogClick(view: View) {
        BaseDialog(TextView(this).apply { setText("测试测试") })
            .showAllowingStateLoss(supportFragmentManager, "1")
        BaseDialog(TextView(this).apply { setText("2222") })
            .showAllowingStateLoss(supportFragmentManager, "1")
    }
}