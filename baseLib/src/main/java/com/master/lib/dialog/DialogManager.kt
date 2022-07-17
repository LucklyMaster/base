package com.master.lib.dialog

/**
 * [BaseDialog]的dismiss管理类
 * @author: MasterChan
 * @date: 2022-07-17 23:34
 */
class DialogManager {

    companion object {
        val instance: DialogManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DialogManager() }
    }

    fun dismissAll() {

    }

    fun dismiss(tag: String?) {
    }

    fun dismiss(dialog: BaseDialog) {
    }
}