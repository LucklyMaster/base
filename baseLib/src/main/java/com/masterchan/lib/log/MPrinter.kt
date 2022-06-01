package com.masterchan.lib.log

import android.util.Log

/**
 * @author: MasterChan
 * @date: 2022-05-28 23:50
 * @describe: Log打印实现
 */
open class MPrinter : IPrinter {

    override fun println(priority: Int, tag: String, content: String?) {
        Log.println(priority, tag, safeMsg(content))
    }

    private fun safeMsg(msg: String?): String {
        return msg ?: "the print content is null"
    }
}