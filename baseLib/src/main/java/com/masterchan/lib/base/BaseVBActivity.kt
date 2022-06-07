package com.masterchan.lib.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 在[BaseActivity]基础上使用[ViewBinding]
 * @author: MasterChan
 * @date: 2022-05-24 23:37
 */
open class BaseVBActivity<T : ViewBinding> : BaseActivity() {

    protected val binding: T by lazy { getViewBinding() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    /**
     * 获取当前Activity的ViewBinding，如果不想用反射可以重写此方法直接返回ViewBinding
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