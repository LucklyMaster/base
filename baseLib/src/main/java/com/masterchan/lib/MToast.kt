package com.masterchan.lib

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import com.masterchan.lib.ext.application
import com.masterchan.lib.ext.isMainThread
import java.lang.ref.WeakReference

/**
 * MToast旨在解决线程Toast、重复Toast问题，如果需要自定义View，
 * 可以参考[com.github.getActivity:ToastUtils]
 * @author: MasterChan
 * @date: 2022-06-08 21:20
 */
object MToast {

    private var toast: WeakReference<Toast>? = null
    private var mainHandler: WeakReference<Handler>? = null
    private var lastMills = 0L
    private var lastText: CharSequence = ""

    fun show(text: CharSequence) {
        show(text, Toast.LENGTH_SHORT)
    }

    fun show(@StringRes text: Int, vararg format: Any) {
        show(application.getString(text, format))
    }

    fun showLong(text: CharSequence) {
        show(text, Toast.LENGTH_LONG)
    }

    fun showLong(@StringRes text: Int, vararg format: Any) {
        show(application.getString(text, format))
    }

    private fun show(text: CharSequence, duration: Int) {
        if (text == lastText) {
            if (System.currentTimeMillis() - lastMills > 2000) {
                safeShow(text, duration)
            }
        } else {
            safeShow(text, duration)
        }
    }

    private fun safeShow(text: CharSequence, duration: Int) {
        if (isMainThread()) {
            doShow(text, duration)
        } else {
            if (mainHandler == null || mainHandler!!.get() == null) {
                mainHandler = WeakReference(Handler(Looper.getMainLooper()))
            }
            mainHandler!!.get()!!.post { doShow(text, duration) }
        }
    }

    private fun doShow(text: CharSequence, duration: Int) {
        toast?.get()?.cancel()
        toast = WeakReference(Toast.makeText(application, "", duration))
        toast!!.get()!!.apply { setText(text) }.show()
        lastText = text
        lastMills = System.currentTimeMillis()
    }
}