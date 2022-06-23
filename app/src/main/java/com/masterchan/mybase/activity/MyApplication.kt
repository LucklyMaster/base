package com.masterchan.mybase.activity

import android.app.Application
import com.mc.lib.MCLib
import com.mc.lib.log.DiskLogManager

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