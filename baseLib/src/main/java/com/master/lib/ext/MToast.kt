package com.master.lib.ext

import android.widget.Toast
import androidx.annotation.StringRes
import com.master.lib.MToast
import com.master.lib.utils.StackTraceUtils

fun toast(text: CharSequence) {
    MToast.show(text, Toast.LENGTH_SHORT, getStackTraceElement())
}

fun toast(@StringRes text: Int, vararg format: Any) {
    MToast.show(application.getString(text, format), Toast.LENGTH_SHORT, getStackTraceElement())
}

fun toastLong(text: CharSequence) {
    MToast.show(text, Toast.LENGTH_LONG, getStackTraceElement())
}

fun toastLong(@StringRes text: Int, vararg format: Any) {
    MToast.show(application.getString(text, format), Toast.LENGTH_LONG, getStackTraceElement())
}

private fun getStackTraceElement(): StackTraceElement? {
    return StackTraceUtils.getTargetStackTraceElement("com.master.lib.ext.MToastKt")
}