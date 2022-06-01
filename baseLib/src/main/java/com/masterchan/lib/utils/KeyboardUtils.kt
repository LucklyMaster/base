package com.masterchan.lib.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager

/**
 * @author MasterChan
 * @date 2021-12-09 09:55
 * @describe 监听键盘的显示和隐藏
 */
class KeyboardUtils private constructor(activity: Activity) {

    private var mDecorViewVisibleHeight = 0
    private var mKeyboardChangeListener: OnKeyboardChangeListener? = null

    companion object {
        fun listen(activity: Activity, onKeyboardChangeListener: OnKeyboardChangeListener) {
            val keyBoardUtils = KeyboardUtils(activity)
            keyBoardUtils.setOnSoftKeyBoardChangeListener(onKeyboardChangeListener)
        }

        fun hide(view: View) {
            val manager = view.context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun show(view: View) {
            val manager = view.context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            view.requestFocus()
            manager.showSoftInput(view, 0)
        }
    }

    interface OnKeyboardChangeListener {
        fun onShow(height: Int)

        fun onHide(height: Int)
    }

    init {
        activity.window.decorView.viewTreeObserver.addOnGlobalLayoutListener(
            OnGlobalLayoutListener {
                //获取当前根视图在屏幕上显示的大小
                val rect = Rect()
                activity.window.decorView.getWindowVisibleDisplayFrame(rect)
                val visibleHeight = rect.height()
                if (mDecorViewVisibleHeight == 0) {
                    mDecorViewVisibleHeight = visibleHeight
                    return@OnGlobalLayoutListener
                }

                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (mDecorViewVisibleHeight == visibleHeight) {
                    return@OnGlobalLayoutListener
                }

                //根视图显示高度变小超过200，可以看作软键盘显示了
                if (mDecorViewVisibleHeight - visibleHeight > 200) {
                    mKeyboardChangeListener?.onShow(mDecorViewVisibleHeight - visibleHeight)
                    mDecorViewVisibleHeight = visibleHeight
                    return@OnGlobalLayoutListener
                }

                //根视图显示高度变大超过200，可以看作软键盘隐藏了
                if (visibleHeight - mDecorViewVisibleHeight > 200) {
                    mKeyboardChangeListener?.onHide(visibleHeight - mDecorViewVisibleHeight)
                    mDecorViewVisibleHeight = visibleHeight
                }
            })
    }

    private fun setOnSoftKeyBoardChangeListener(
        onKeyboardChangeListener: OnKeyboardChangeListener
    ) {
        this.mKeyboardChangeListener = onKeyboardChangeListener
    }
}