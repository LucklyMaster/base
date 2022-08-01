package com.master.mybase.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.master.base.BaseVBActivity
import com.master.ext.Log

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