package com.masterchan.mybase.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.master.lib.ext.*
import com.master.lib.log.MLog
import com.master.lib.utils.DeviceUtils
import com.masterchan.mybase.databinding.ActivityMainBinding


class MainActivity : MyBaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        printAppInfo()
        setOnClickListeners(
            this, binding.keyboard, binding.imageUtils, binding.scoped, binding.dialog,
            binding.permissions
        )
    }

    private fun printAppInfo() {
        MLog.d(
            "screenWidth:$screenWidth\n" +
            "screenHeight:$screenHeight\n" +
            "screenRealHeight:$screenRealHeight\n" +
            "statusBarHeight:$statusBarHeight\n" +
            "navigationBarHeight:$navigationBarHeight\n" +
            "orientation:$orientation\n" +
            "versionName:$versionName\n" +
            "versionCode:$versionCode\n" +
            "isRooted:${DeviceUtils.isRooted()}"
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onClick(v: View?) {
        val clazz = when (v) {
            binding.keyboard -> AutoFoldKeyboardActivity::class.java
            binding.imageUtils -> ShapeViewActivity::class.java
            binding.scoped -> ScopedStorageActivity::class.java
            binding.dialog -> DialogActivity::class.java
            binding.permissions -> PermissionsActivity::class.java
            else -> MainActivity::class.java
        }
        startActivity(clazz)
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}