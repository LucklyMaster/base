package com.master

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**
 * Activity栈管理
 * @author: MasterChan
 * @date: 2022-05-28 16:23
 */
class ActivityStack {

    private val stack by lazy { Stack<Activity>() }
    internal var isAppForeground = false
    internal var currentActivity: Activity? = null

    companion object {
        val instance: ActivityStack by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ActivityStack() }
    }

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallback() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                stack.add(activity)
                currentActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {
                isAppForeground = true
            }

            override fun onActivityPaused(activity: Activity) {
                isAppForeground = false
            }

            override fun onActivityDestroyed(activity: Activity) {
                stack.remove(activity)
                currentActivity = null
            }
        })
    }

    /**
     * 获取栈底
     * @return Activity?
     */
    fun bottom(): Activity? {
        return try {
            stack.firstElement()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取栈顶
     * @return Activity?
     */
    fun top(): Activity? {
        return try {
            stack.lastElement()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 判断Activity是否在栈中
     * @param activity Activity
     * @return Boolean
     */
    fun inStack(activity: Activity): Boolean {
        return stack.contains(activity)
    }

    fun inStack(clazz: Class<out Activity>): Boolean {
        return indexOf(clazz) >= 0
    }

    /**
     * 获取Activity在栈中的索引
     * @param clazz Class<out Activity>
     * @return Int
     */
    fun indexOf(clazz: Class<out Activity>): Int {
        stack.forEachIndexed { index, it ->
            if (it.javaClass == clazz) {
                return index
            }
        }
        return -1
    }

    fun finish(clazz: Class<out Activity>) {
        stack.forEach { if (it.javaClass == clazz) it.finish() }
    }

    /**
     * 关闭指定Activity之后的页面
     * @param clazz Class<out Activity>
     * @param withSelf 是否关闭自己
     */
    fun finishTop(clazz: Class<out Activity>, withSelf: Boolean = false) {
        finishTop(indexOf(clazz), withSelf)
    }

    fun finishTop(activity: Activity, withSelf: Boolean = false) {
        finishTop(stack.indexOf(activity), withSelf)
    }

    /**
     * 关闭索引对应Activity之后的页面
     * @param indexOf Activity在栈中的索引
     * @param withSelf 是否关闭自己
     */
    fun finishTop(indexOf: Int, withSelf: Boolean) {
        stack.toList()
            .filterIndexed { index, _ -> if (withSelf) index >= indexOf else index > indexOf }
            .reversed()
            .forEach { it.finish() }
    }

    /**
     * 按照先进先出的规则关闭全部Activity
     * @param reversed 按照先进后出的规则关闭
     */
    fun finishAll(reversed: Boolean = false) {
        with(stack.toList()) { if (reversed) reversed() else this }.forEach { it.finish() }
    }
}