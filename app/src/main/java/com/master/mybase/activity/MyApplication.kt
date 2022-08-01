package com.master.mybase.activity

import android.app.Application
import com.master.MCLib
import com.master.log.DiskLogManager

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
        MCLib.init(this)
            .setLog { setLogManager(DiskLogManager()) }
            .setCrashHandler {
                saveCrash(true).init()
            }
    }

}