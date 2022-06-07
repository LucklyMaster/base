package com.masterchan.lib.log

import android.util.Log

/**
 * Log打印实现
 * @author: MasterChan
 * @date: 2022-05-28 23:50
 */
open class MPrinter : IPrinter {

    override fun println(priority: Int, tag: String, content: String) {
        Log.println(priority, tag, content)
    }
}