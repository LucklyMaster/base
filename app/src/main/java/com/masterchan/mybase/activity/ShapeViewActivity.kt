package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import com.master.lib.ext.dp2px
import com.master.lib.view.IShapeView
import com.masterchan.mybase.databinding.ActivityShapeViewBinding

class ShapeViewActivity : MyBaseActivity<ActivityShapeViewBinding>() {

    override fun onCreated(savedInstanceState: Bundle?) {
    }

    fun onClick(v: View) {
        // (v as IShapeView).shapeHelper.setLeftTopRadius(dp2px(10)).into()
    }

    fun onClick1(v: View) {
        // (v as IShapeView).shapeHelper.setRightTopRadius(dp2px(30)).into()
    }
}