package com.master.lib.permission

/**
 * 权限申请回调
 * @author: MasterChan
 * @date: 2022-07-24 16:50
 */
fun interface Callback {
    fun callback(response: Response)
}