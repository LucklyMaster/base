package com.masterchan.mybase

import android.app.Application
import com.masterchan.lib.MCLib
import com.masterchan.lib.log.DiskLogManager

/**
 * @author: MasterChan
 * @date: 2022-05-26 22:39
 * @describe:
 */
class MyApplication : Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MCLib.init(this).setLogConfig {
            setLogManager(DiskLogManager())
        }
    }

}