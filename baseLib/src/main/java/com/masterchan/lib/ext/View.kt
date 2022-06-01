package com.masterchan.lib.ext

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.LayoutRes
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

/**
 * @author MasterChan
 * @date 2021-12-06 14:03
 * @describe ViewExtension
 */

/**
 * 获取View的Context对应的Activity
 * @receiver View
 * @return Activity?
 */
val View.activity: Activity?
    get() {
        return context.toActivity()
    }

fun View.px2dp(dp: Float): Float {
    return context.px2dp(dp)
}

fun View.dp2px(dp: Float): Float {
    return context.dp2px(dp)
}

fun View.setPaddingLeft(paddingLeft: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingTop(paddingTop: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingRight(paddingRight: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.setPaddingBottom(paddingBottom: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
}

fun View.startAnimation(@AnimRes animRes: Int) {
    startAnimation(AnimationUtils.loadAnimation(context, animRes))
}

fun ViewGroup.inflater(
    @LayoutRes layoutId: Int, root: ViewGroup = this, attachToRoot: Boolean = false
): View {
    return LayoutInflater.from(context).inflate(layoutId, root, attachToRoot)
}

fun View.gone() {
    isGone = true
}

fun ViewGroup.aa() {
    children
}

fun View.visible() {
    isVisible = true
}

fun View.invisible() {
    isInvisible = true
}