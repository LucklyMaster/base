package com.mc.lib.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

/**
 * BaseFragment
 * @author: MasterChan
 * @date: 2022-05-25 21:20
 */
open class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    protected lateinit var activity: Activity
    protected lateinit var rootView: View

    /**
     * window控制器
     */
    protected var windowController: WindowInsetsControllerCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity()
        rootView = getContentView() ?: super.onCreateView(inflater, container, savedInstanceState)!!
        windowController = ViewCompat.getWindowInsetsController(activity.window.decorView)
        return rootView
    }

    protected fun <T : View> findViewById(@IdRes id: Int): T {
        return rootView.findViewById(id)
    }

    protected open fun getContentView(): View? {
        return null
    }
}