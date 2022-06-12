package com.masterchan.lib.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.StringRes

fun dp2px(dp: Float): Float = (dp * application.resources.displayMetrics.density + 0.5f)

fun px2dp(px: Float): Float = (px / application.resources.displayMetrics.density + 0.5f)

fun dp2pxi(dp: Float): Int = (dp * application.resources.displayMetrics.density + 0.5f).toInt()

fun px2dpi(px: Float): Int = (px / application.resources.displayMetrics.density + 0.5f).toInt()

/**
 * 屏幕宽度
 */
val screenWidth: Int
    get() {
        Log.d("获取一次")
        return application.resources.displayMetrics.widthPixels
    }

/**
 * 屏幕高度
 */
val screenHeight: Int
    get() {
        return application.resources.displayMetrics.heightPixels
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

/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
    WindowInsets windowInsets = windowMetrics.getWindowInsets();
    Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());
    return insets.top;
}*/

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
 * 获取String集合
 * @receiver Context
 * @param resources StringRes
 * @return List<String>
 */
fun Context.getStringList(@StringRes vararg resources: Int): List<String> {
    return resources.map { getString(it) }
}

/**
 * 获取String数组
 * @receiver Context
 * @param resources IntArray
 * @return Array<String>
 */
fun Context.getStringArray(@StringRes vararg resources: Int): Array<String> {
    return getStringList(*resources).toTypedArray()
}

/**
 * 获取Context对应的Activity
 * @receiver Context
 * @return Activity?
 */
fun Context.toActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = (this as ContextWrapper).baseContext
    }
    return null
}