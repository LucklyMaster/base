package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import com.masterchan.lib.ext.setOnClickListeners
import com.masterchan.lib.ext.startActivity
import com.masterchan.mybase.databinding.ActivityMainBinding

class MainActivity : MyBaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onCreated(savedInstanceState: Bundle?) {
        setOnClickListeners(this, binding.keyboard, binding.imageUtils, binding.scoped)
    }

    override fun onClick(v: View?) {
        val clazz = when (v) {
            binding.keyboard -> AutoFoldKeyboardActivity::class.java
            binding.imageUtils -> ImageUtilsActivity::class.java
            binding.scoped -> ScopedStorageActivity::class.java
            else -> MainActivity::class.java
        }
        startActivity(clazz)
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}