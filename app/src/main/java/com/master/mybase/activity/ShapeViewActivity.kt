package com.master.mybase.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.master.lib.view.EditableCellView
import com.master.mybase.databinding.ActivityShapeViewBinding

class ShapeViewActivity : MyBaseActivity<ActivityShapeViewBinding>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
    }

    fun onClick(v: View) {
        binding.shapeImageView.apply { scaleType = ImageView.ScaleType.CENTER_CROP }
            .setForceSquare(!binding.shapeImageView.isForceSquare)
            .setSquareBy(0)
    }

    fun editTextClick(view: View) {
    }

    fun toggleEdit(view: View) {
        val cellView = view as EditableCellView
        cellView.setEditable(!cellView.isEditable)
    }
}