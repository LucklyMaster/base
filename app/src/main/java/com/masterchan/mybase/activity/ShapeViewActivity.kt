package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.master.lib.view.ShapeImageView
import com.masterchan.mybase.databinding.ActivityShapeViewBinding

class ShapeViewActivity : MyBaseActivity<ActivityShapeViewBinding>() {

    override fun onCreated(savedInstanceState: Bundle?) {
    }

    fun onClick(v: View) {
        binding.shapeImageView.apply { scaleType = ImageView.ScaleType.CENTER_CROP }
            .setForceSquare(!binding.shapeImageView.isForceSquare)
            .setSquareBy(0)
    }
}