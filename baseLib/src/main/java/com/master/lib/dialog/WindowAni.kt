package com.master.lib.dialog

/**
 * BaseDialog的进出场动画
 * @author MasterChan
 * @date 2021-12-29 09:40
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class WindowAni {
    companion object {
        const val START = 0
        const val TOP = 1
        const val END = 2
        const val BOTTOM = 3
        const val CENTER = 4
    }
}