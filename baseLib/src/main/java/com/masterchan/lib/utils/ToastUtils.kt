package com.masterchan.lib.utils

import android.widget.Toast
import com.masterchan.lib.MCLib

object ToastUtils {

    private val context by lazy { MCLib.application }
    private var isRedo = false
    private var toast: Toast? = null
    private var message = ""

    fun show(msg: String, duration: Int = Toast.LENGTH_LONG) {
        if (isRedo) {
            toast = Toast.makeText(context, "", duration)
            toast!!.setText(msg)
            toast!!.show()
        } else {
            if (toast == null) {
                toast = Toast.makeText(context, "", duration)
            }
            if (message === msg) {
                toast?.setText(msg)
            } else {
                toast?.cancel()
                toast?.setText(msg)
            }
            toast!!.show()
        }
        message = msg
    }

    fun setRedo(redo: Boolean) = apply { isRedo = redo }
}