package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.master.lib.dialog.BaseDialog
import com.master.lib.dialog.WindowAni
import com.masterchan.mybase.R
import com.masterchan.mybase.databinding.ActivityDialogBinding

class DialogActivity : MyBaseActivity<ActivityDialogBinding>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    fun baseDialogClick(view: View) {
        // BaseDialog(this, TextView(this).apply { setText("测试测试") }).setGravity(Gravity.CENTER)
        //     .setAnimate(WindowAni.BOTTOM)
        //     .setCanceledOnTouchOutside(false)
        //     .setCancellable(true)
        //     .show()
        BaseDialog(this, layoutRes = R.layout.activity_dialog).setGravity(Gravity.CENTER)
            .setAnimate(WindowAni.BOTTOM)
            .setCanceledOnTouchOutside(false)
            .setCancellable(true)
            .show()
    }
}