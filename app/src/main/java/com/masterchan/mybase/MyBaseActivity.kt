package com.masterchan.mybase

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewbinding.ViewBinding
import com.masterchan.lib.base.BaseVBActivity
import com.masterchan.lib.ext.Log

/**
 * @author: MasterChan
 * @date: 2022-05-26 20:56
 * @describe:
 */
abstract class MyBaseActivity<T : ViewBinding> : BaseVBActivity<T>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d("onCreate: ${javaClass.simpleName}")
        onCreated(savedInstanceState)
    }

    abstract fun onCreated(savedInstanceState: Bundle?)

}