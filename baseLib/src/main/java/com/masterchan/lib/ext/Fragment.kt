package com.masterchan.lib.ext

import androidx.fragment.app.Fragment

/**
 * @author MasterChan
 * @date 2021-12-06 14:00
 * @describe Activity相关的扩展函数
 */

fun Fragment.dp2px(dp: Float): Float {
    return requireContext().dp2px(dp)
}

fun Fragment.px2dp(dp: Float): Float {
    return requireContext().px2dp(dp)
}