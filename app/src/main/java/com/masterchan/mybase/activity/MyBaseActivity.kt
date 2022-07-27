package com.masterchan.mybase.activity

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewbinding.ViewBinding
import com.master.lib.base.BaseVBActivity
import com.master.lib.ext.Log

/**
 * @author: MasterChan
 * @date: 2022-05-26 20:56
 * @describe:
 */
abstract class MyBaseActivity<T : ViewBinding> : BaseVBActivity<T>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("onCreate: ${javaClass.simpleName}")
        onActivityCreated(savedInstanceState)
    }

    abstract fun onActivityCreated(savedInstanceState: Bundle?)

}