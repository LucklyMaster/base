package com.mc.lib.base

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 在[BaseFragment]基础上使用[ViewBinding]
 * @author: MasterChan
 * @date: 2022-05-25 21:39
 */
abstract class BaseVBFragment<T : ViewBinding> : BaseFragment(0) {

    protected val binding: T by lazy { getViewBinding() }

    override fun getContentView(): View? {
        return binding.root
    }

    /**
     * 获取当前Fragment的ViewBinding，如果不想用反射可以重写此方法直接返回ViewBinding
     * @return ViewBinding
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun getViewBinding(): T {
        val type = javaClass.genericSuperclass as ParameterizedType
        val clazz = type.actualTypeArguments[0] as Class<*>
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, layoutInflater) as T
    }
}