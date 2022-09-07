package com.master.mybase.activity

import android.graphics.Color
import android.os.Bundle
import com.master.lib.base.BaseVBActivity
import com.master.lib.dialog.BottomSheetDialog
import com.master.lib.ext.Log
import com.master.mybase.databinding.ActivitySheetLayoutBinding

class SheetLayoutActivity : BaseVBActivity<ActivitySheetLayoutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding.sheetLayout.setOnStateChangedListener {
        //     Log.d("state = $it")
        // }
        binding.btnSheet.setOnClickListener {
            BottomSheetDialog(this).show()
        }
    }

}