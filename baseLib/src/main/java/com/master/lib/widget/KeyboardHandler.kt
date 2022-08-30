package com.master.lib.widget

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager

/**
 * 监听键盘的显示和隐藏
 * @author MasterChan
 * @date 2021-12-09 09:55
 */
class KeyboardHandler private constructor(activity: Activity) {

    private var decorViewVisibleHeight = 0
    private var keyboardChangeListener: OnKeyboardChangedListener? = null

    companion object {

        @JvmStatic
        fun registerListener(
            activity: Activity,
            onKeyboardChangedListener: OnKeyboardChangedListener
        ) {
            val keyBoardHandler = KeyboardHandler(activity)
            keyBoardHandler.setOnKeyboardChangedListener(onKeyboardChangedListener)
        }

        @JvmStatic
        fun hide(view: View) {
            val manager = view.context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        @JvmStatic
        fun show(view: View) {
            val manager = view.context.getSystemService(InputMethodManager::class.java)
            view.requestFocus()
            manager.showSoftInput(view, 0)
        }
    }

    interface OnKeyboardChangedListener {
        fun onShow(height: Int, handler: KeyboardHandler)

        fun onHide(height: Int, handler: KeyboardHandler)
    }

    init {
        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (keyboardChangeListener == null) {
                    activity.window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                //获取当前根视图在屏幕上显示的大小
                val rect = Rect()
                activity.window.decorView.getWindowVisibleDisplayFrame(rect)
                val visibleHeight = rect.height()
                if (decorViewVisibleHeight == 0) {
                    decorViewVisibleHeight = visibleHeight
                    return
                }

                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (decorViewVisibleHeight == visibleHeight) {
                    return
                }

                //根视图显示高度变小超过200，可以看作软键盘显示了
                if (decorViewVisibleHeight - visibleHeight > 200) {
                    keyboardChangeListener?.onShow(
                        decorViewVisibleHeight - visibleHeight, this@KeyboardHandler
                    )
                    decorViewVisibleHeight = visibleHeight
                    return
                }

                //根视图显示高度变大超过200，可以看作软键盘隐藏了
                if (visibleHeight - decorViewVisibleHeight > 200) {
                    keyboardChangeListener?.onHide(
                        visibleHeight - decorViewVisibleHeight, this@KeyboardHandler
                    )
                    decorViewVisibleHeight = visibleHeight
                }
            }
        })
    }

    private fun setOnKeyboardChangedListener(
        onKeyboardChangedListener: OnKeyboardChangedListener
    ) {
        this.keyboardChangeListener = onKeyboardChangedListener
    }

    fun unregisterListener() {
        keyboardChangeListener = null
    }
}