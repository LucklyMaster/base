package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import com.master.lib.ext.logD
import com.master.lib.utils.NetUtils
import com.masterchan.mybase.databinding.ActivityImageUtilsBinding

class ImageUtilsActivity : MyBaseActivity<ActivityImageUtilsBinding>(), View.OnClickListener {

    override fun onCreated(savedInstanceState: Bundle?) {
        setOnViewMultiClickListeners(this) {
            arrayOf(btnSelector, btnLoad)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnSelector -> {
                // var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.img)
                // bitmap = bitmap.toRound(20f, Color.RED)
                // binding.ivImage.setImageBitmap(bitmap)
                NetUtils.getIpAddress(true).logD()
                NetUtils.getIpAddress(false).logD()
                NetUtils.getConnectType().logD()
            }
            binding.btnLoad -> {
                // var bitmap = BitmapFactory.decodeResource(resources, R.mipmap.img)
                // bitmap = bitmap.toBlur(0.4f, 25f)
                // binding.ivImage.setImageBitmap(bitmap)
            }
        }
    }
}