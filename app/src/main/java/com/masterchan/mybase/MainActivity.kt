package com.masterchan.mybase

import android.os.Bundle
import android.view.View
import com.masterchan.lib.ext.Log
import com.masterchan.lib.ext.setOnClickListeners
import com.masterchan.lib.ext.startActivity
import com.masterchan.mybase.databinding.ActivityMainBinding
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


class MainActivity : MyBaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onCreated(savedInstanceState: Bundle?) {
        setOnClickListeners(this, binding.keyboard, binding.imageUtils)
        Logger.addLogAdapter(AndroidLogAdapter())
        Log.d("启动：" + System.currentTimeMillis())
    }

    override fun onClick(v: View?) {
        val clazz = when (v) {
            binding.keyboard -> AutoFoldKeyboardActivity::class.java
            binding.imageUtils -> ImageUtilsActivity::class.java
            else -> MainActivity::class.java
        }
        startActivity(clazz)
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}