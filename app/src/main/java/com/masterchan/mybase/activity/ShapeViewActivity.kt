package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.master.lib.ext.dp2pxi
import com.master.lib.view.TitleBar
import com.masterchan.mybase.R
import com.masterchan.mybase.databinding.ActivityShapeViewBinding

class ShapeViewActivity : MyBaseActivity<ActivityShapeViewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    fun onClick(v: View) {
        binding.shapeImageView.apply { scaleType = ImageView.ScaleType.CENTER_CROP }
            .setForceSquare(!binding.shapeImageView.isForceSquare)
            .setSquareBy(0)
    }

    fun editTextClick(view: View) {
        val bar = view as TitleBar
        bar.middleItem.setText("老师富婆阿萨德副科级奥围观")
        // bar.middleItem.setIcon(R.mipmap.ic_launcher)
        // bar.middleItem.setIconSize(199, 199)
        // binding.tvText.text = "是饭碗色法"
    }
}