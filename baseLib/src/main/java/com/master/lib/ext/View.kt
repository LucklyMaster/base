package com.master.lib.ext

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

val View.activity: Activity?
    get() = context.activity

fun View.setOnOnceClickListener(listener: View.OnClickListener) {
    setOnOnceClickListener {
        listener.onClick(it)
    }
}

/**
 * 多次点击只有一次生效
 * @receiver View
 * @param delay Int
 * @param listener Function1<[@kotlin.ParameterName] View, Unit>
 */
fun View.setOnOnceClickListener(delay: Int = 500, listener: (View: View) -> Unit) {
    setOnClickListener {
        val tagKey = Int.MAX_VALUE - 1001
        val tag = it.getTag(tagKey)
        if (tag != null && System.currentTimeMillis() - (tag as Long) < delay) {
            return@setOnClickListener
        }
        listener.invoke(it)
        it.setTag(tagKey, System.currentTimeMillis())
    }
}

fun View.setWidth(width: Int) {
    var lp = layoutParams
    if (lp == null) {
        lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    lp.width = width
    layoutParams = lp
}

fun View.setHeight(height: Int) {
    var lp = layoutParams
    if (lp == null) {
        lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    lp.height = height
    layoutParams = lp
}

fun View.setSize(width: Int, height: Int) {
    var lp = layoutParams
    if (lp == null) {
        lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    lp.width = width
    lp.height = height
    layoutParams = lp
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