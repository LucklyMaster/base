package com.master.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics

fun dp2px(dp: Float): Float = dp * displayDensity + 0.5f
fun dp2px(dp: Int): Float = dp * displayDensity + 0.5f
fun px2dp(px: Float): Float = px / displayDensity + 0.5f
fun px2dp(px: Int): Float = px / displayDensity + 0.5f
fun dp2pxi(dp: Float): Int = (dp * displayDensity + 0.5f).toInt()
fun dp2pxi(dp: Int): Int = (dp * displayDensity + 0.5f).toInt()
fun px2dpi(px: Float): Int = (px / displayDensity + 0.5f).toInt()
fun px2dpi(px: Int): Int = (px / displayDensity + 0.5f).toInt()
fun Context.dp2px(dp: Float): Float = (dp * resources.displayMetrics.density + 0.5f)
fun Context.dp2px(dp: Int): Float = (dp * resources.displayMetrics.density + 0.5f)
fun Context.px2dp(px: Float): Float = (px / resources.displayMetrics.density + 0.5f)
fun Context.px2dp(px: Int): Float = (px / resources.displayMetrics.density + 0.5f)
fun Context.dp2pxi(dp: Float): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()
fun Context.dp2pxi(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()
fun Context.px2dpi(px: Float): Int = (px / resources.displayMetrics.density + 0.5f).toInt()
fun Context.px2dpi(px: Int): Int = (px / resources.displayMetrics.density + 0.5f).toInt()

fun sp2px(sp: Float): Int = (sp * scaledDensity + 0.5f).toInt()
fun sp2px(sp: Int): Int = (sp * scaledDensity + 0.5f).toInt()
fun px2sp(px: Float) = (px / scaledDensity + 0.5f).toInt()
fun px2sp(px: Int) = (px / scaledDensity + 0.5f).toInt()
fun Context.sp2px(sp: Float): Int = (sp * scaledDensity + 0.5f).toInt()
fun Context.sp2px(sp: Int): Int = (sp * scaledDensity + 0.5f).toInt()
fun Context.px2sp(px: Float) = (px / scaledDensity + 0.5f).toInt()
fun Context.px2sp(px: Int) = (px / scaledDensity + 0.5f).toInt()

/**
 * 屏幕宽度
 */
val screenWidth: Int
    get() {
        return application.resources.displayMetrics.widthPixels
    }

/**
 * 屏幕高度，未包含系统装饰
 */
val screenHeight: Int
    get() {
        return application.resources.displayMetrics.heightPixels
    }

/**
 * 屏幕物理高度，即真正的高度
 */
val screenRealHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            topActivity?.windowManager?.currentWindowMetrics?.bounds?.height() ?: 0
        } else {
            val metrics = DisplayMetrics()
            topActivity?.windowManager?.defaultDisplay?.getRealMetrics(metrics)
            metrics.heightPixels
        }
    }

/**
 * 状态栏高度
 */
val statusBarHeight: Int
    get() {
        val resourceId = application.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return application.resources.getDimensionPixelSize(resourceId)
    }

/**
 * 导航栏高度
 */
val navigationBarHeight: Int
    get() {
        val resourceId = application.resources.getIdentifier(
            "navigation_bar_height", "dimen", "android"
        )
        return application.resources.getDimensionPixelSize(resourceId)
    }

/**
 * 像素密度
 */
val displayDensity: Float
    get() = application.resources.displayMetrics.density

val scaledDensity: Float
    get() = application.resources.displayMetrics.scaledDensity

/**
 * 屏幕方向，取值为[Configuration.ORIENTATION_LANDSCAPE]、[Configuration.ORIENTATION_PORTRAIT]、
 * [Configuration.ORIENTATION_UNDEFINED]
 */
val orientation: Int
    get() {
        return application.resources.configuration.orientation
    }

/**
 * 是否是竖屏
 */
val isPortrait: Boolean
    get() {
        return orientation == Configuration.ORIENTATION_PORTRAIT
    }

/**
 * 是否是横屏
 */
val isLandscape: Boolean
    get() {
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }

/**
 * 包名
 */
val packageName: String
    get() {
        return application.packageName
    }

/**
 * 版本号
 */
val versionCode: Long
    get() {
        val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
    }

/**
 * 版本名
 */
val versionName: String
    get() {
        return application.packageManager.getPackageInfo(application.packageName, 0).versionName
    }

/**
 * 获取Context对应的Activity
 * @receiver Context
 * @return Activity?
 */
val Context.activity: Activity?
    get() {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = (this as ContextWrapper).baseContext
        }
        return null
    }

fun Activity.startActivity(clazz: Class<out Activity>) {
    startActivity(Intent(this, clazz))
}

inline fun <reified T : Activity> Activity.startActivity(params: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    params.invoke(intent)
    startActivity(intent)
}