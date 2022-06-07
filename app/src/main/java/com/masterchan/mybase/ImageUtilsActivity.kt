package com.masterchan.mybase

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.masterchan.lib.ActivityStack
import com.masterchan.mybase.databinding.ActivityImageUtilsBinding

class ImageUtilsActivity : MyBaseActivity<ActivityImageUtilsBinding>() {

    private lateinit var toast: Toast

    override fun onCreated(savedInstanceState: Bundle?) {
        toast = Toast.makeText(this, "aaa", Toast.LENGTH_LONG)
    }

    fun toast(view: View) {
        throw ArithmeticException("空指针异常")
        // ActivityStack.instance.inStack(this::class.java)
    }

    fun setText(view: View) {
        ActivityStack.instance.finishTop(this, true)
    }
}