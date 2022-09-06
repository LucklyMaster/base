package com.master.lib.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

fun dp2px(dp: Float): Float = dp * application.displayDensity + 0.5f
fun dp2px(dp: Int): Float = dp * application.displayDensity + 0.5f
fun px2dp(px: Float): Float = px / application.displayDensity + 0.5f
fun px2dp(px: Int): Float = px / application.displayDensity + 0.5f
fun dp2pxi(dp: Float): Int = (dp * application.displayDensity + 0.5f).toInt()
fun dp2pxi(dp: Int): Int = (dp * application.displayDensity + 0.5f).toInt()
fun px2dpi(px: Float): Int = (px / application.displayDensity + 0.5f).toInt()
fun px2dpi(px: Int): Int = (px / application.displayDensity + 0.5f).toInt()

fun sp2px(sp: Float): Float = sp * application.scaledDensity + 0.5f
fun sp2px(sp: Int): Float = sp * application.scaledDensity + 0.5f
fun px2sp(px: Float) = px / application.scaledDensity + 0.5f
fun px2sp(px: Int) = px / application.scaledDensity + 0.5f
fun sp2pxi(sp: Float): Int = (sp * application.scaledDensity + 0.5f).toInt()
fun sp2pxi(sp: Int): Int = (sp * application.scaledDensity + 0.5f).toInt()
fun px2spi(px: Float) = (px / application.scaledDensity + 0.5f).toInt()
fun px2spi(px: Int) = (px / application.scaledDensity + 0.5f).toInt()

fun Context.dp2px(dp: Float): Float = dp * resources.displayMetrics.density + 0.5f
fun Context.dp2px(dp: Int): Float = dp * resources.displayMetrics.density + 0.5f
fun Context.px2dp(px: Float): Float = px / resources.displayMetrics.density + 0.5f
fun Context.px2dp(px: Int): Float = px / resources.displayMetrics.density + 0.5f
fun Context.dp2pxi(dp: Float): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()
fun Context.dp2pxi(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()
fun Context.px2dpi(px: Float): Int = (px / resources.displayMetrics.density + 0.5f).toInt()
fun Context.px2dpi(px: Int): Int = (px / resources.displayMetrics.density + 0.5f).toInt()

fun Context.sp2px(sp: Float): Float = sp * resources.displayMetrics.density + 0.5f
fun Context.sp2px(sp: Int): Float = sp * resources.displayMetrics.density + 0.5f
fun Context.px2sp(px: Float) = px / resources.displayMetrics.density + 0.5f
fun Context.px2sp(px: Int) = px / resources.displayMetrics.density + 0.5f
fun Context.sp2pxi(sp: Float): Int = (sp * resources.displayMetrics.density + 0.5f).toInt()
fun Context.sp2pxi(sp: Int): Int = (sp * resources.displayMetrics.density + 0.5f).toInt()
fun Context.px2spi(px: Float) = (px / resources.displayMetrics.density + 0.5f).toInt()
fun Context.px2spi(px: Int) = (px / resources.displayMetrics.density + 0.5f).toInt()

/**
 * 屏幕宽度
 */
val Context.screenWidth: Int
    get() {
        return resources.displayMetrics.widthPixels
    }

/**
 * 屏幕高度，未包含系统装饰
 */
val Context.screenHeight: Int
    get() {
        return resources.displayMetrics.heightPixels
    }

/**
 * 屏幕物理高度，即真正的高度
 */
val Context.screenRealHeight: Int
    get() {
        val manager = getSystemService(WindowManager::class.java)
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            manager?.currentWindowMetrics?.bounds?.height() ?: 0
        } else {
            val metrics = DisplayMetrics()
            manager?.defaultDisplay?.getRealMetrics(metrics)
            metrics.heightPixels
        }
    }

/**
 * 状态栏高度
 */
val Context.statusBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return application.resources.getDimensionPixelSize(resourceId)
    }

/**
 * 导航栏高度
 */
val Context.navigationBarHeight: Int
    get() {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return application.resources.getDimensionPixelSize(resourceId)
    }

/**
 * 像素密度
 */
val Context.displayDensity: Float
    get() = resources.displayMetrics.density

val Context.scaledDensity: Float
    get() = resources.displayMetrics.scaledDensity

/**
 * 屏幕方向，取值为[Configuration.ORIENTATION_LANDSCAPE]、[Configuration.ORIENTATION_PORTRAIT]、
 * [Configuration.ORIENTATION_UNDEFINED]
 */
val Context.orientation: Int
    get() {
        return resources.configuration.orientation
    }

/**
 * 是否是竖屏
 */
val Context.isPortrait: Boolean
    get() {
        return orientation == Configuration.ORIENTATION_PORTRAIT
    }

/**
 * 是否是横屏
 */
val Context.isLandscape: Boolean
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
val Context.versionCode: Long
    get() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
    }

/**
 * 版本名
 */
val Context.versionName: String
    get() {
        return packageManager.getPackageInfo(packageName, 0).versionName
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