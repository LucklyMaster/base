package com.master.lib.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.master.lib.widget.ActivityResultHelper

/**
 * BaseActivity
 * @author: MasterChan
 * @date: 2022-05-24 22:11
 */
open class BaseActivity : AppCompatActivity() {

    protected lateinit var context: Context
    protected lateinit var activity: Activity
    protected open var isAutoFoldKeyboard = false
    protected open var windowController: WindowInsetsControllerCompat? = null
    protected open val activityResultHelper = ActivityResultHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        activity = this
        windowController = ViewCompat.getWindowInsetsController(window.decorView)
        activityResultHelper.register(this)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (isAutoFoldKeyboard && ev.action == MotionEvent.ACTION_DOWN) {
            if (isHideInput(currentFocus, ev)) {
                hideKeyboard()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 判断当前触摸区域是否在[view]上
     * @param view View?
     * @param event MotionEvent
     * @return Boolean
     */
    private fun isHideInput(view: View?, event: MotionEvent): Boolean {
        if (view is EditText) {
            val location = intArrayOf(0, 0)
            view.getLocationInWindow(location)
            val left = location[0]
            val top = location[1]
            val bottom = top + view.getHeight()
            val right = left + view.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    protected open fun hideKeyboard() {
        windowController?.hide(WindowInsetsCompat.Type.ime())
    }

    open fun startActivityForResult(clazz: Class<out Activity>, result: ActivityResult.() -> Unit) {
        activityResultHelper.launch(clazz, result)
    }

    open fun startActivityForResult(intent: Intent, result: ActivityResult.() -> Unit) {
        activityResultHelper.launch(intent, result)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityResultHelper.unregister()
    }
}