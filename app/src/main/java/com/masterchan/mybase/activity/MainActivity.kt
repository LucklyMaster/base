package com.masterchan.mybase.activity

import android.os.Bundle
import android.view.View
import com.masterchan.lib.ext.*
import com.masterchan.lib.log.MLog
import com.masterchan.mybase.databinding.ActivityMainBinding

class MainActivity : MyBaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onCreated(savedInstanceState: Bundle?) {
        printAppInfo()
        setOnClickListeners(this, binding.keyboard, binding.imageUtils, binding.scoped)
    }

    private fun printAppInfo() {
        MLog.d(
            "screenWidth:$screenWidth\n" +
            "screenHeight:$screenHeight\n" +
            "statusBarHeight:$statusBarHeight\n" +
            "navigationBarHeight:$navigationBarHeight\n" +
            "orientation:$orientation\n" +
            "versionName:$versionName\n" +
            "versionCode:$versionCode"
        )
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