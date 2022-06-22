package com.masterchan.lib.ext

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.LayoutRes
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

fun View.activity(): Activity? = context.toActivity()

fun View.setSingleClickListener(listener: View.OnClickListener) {
    setOnClickListener {
        val tagKey = Int.MAX_VALUE - 1001
        if (it.getTag(tagKey) != null) {
            return@setOnClickListener
        }
        it.setTag(tagKey, "singleClick")
        listener.onClick(it)
        it.setTag(tagKey, null)
    }
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

fun View.gone() = run { isGone = true }

fun View.visible() = run { isVisible = true }

fun View.invisible() = run { isInvisible = true }