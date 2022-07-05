package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import com.masterchan.mybase.databinding.ActivityShapeViewBinding

class ShapeViewActivity : MyBaseActivity<ActivityShapeViewBinding>() {

    override fun onCreated(savedInstanceState: Bundle?) {
    }

    fun onClick(v: View) {
        binding.shapeImageView.setCircle(true).setForceSquare(true)
    }
}