package com.master.lib.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.master.lib.R
import com.master.lib.ext.*

/**
 * BaseDialog
 * @author MasterChan
 * @date 2021-12-14 14:30
 */
open class BaseDialog @JvmOverloads constructor(
    context: Context, var contentView: View? = null
) : DialogFragment() {

    protected open val mActivity: FragmentActivity
    protected open var dialogTheme = 0
    protected open var windowDrawable: Drawable? = null
    protected open var windowColor = Color.WHITE
    protected open var windowRadius = 6f
    protected open var windowAmount = 0.6f
    protected open var windowWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    protected open var windowHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    protected open var windowGravity = Gravity.CENTER
    protected open var windowAnimate: Int? = null
    protected open var windowElevation = dp2px(10f)
    protected open var windowBehindBlurRadius = 0
    protected open var contentBlurRadius = 0
    protected open var xOffset = 0
    protected open var yOffset = 0
    protected open var cancellable = true
    protected open var canceledOnTouchOutside = true
    protected open var dismissListener: DialogInterface.OnDismissListener? = null
    protected open var cancelListener: DialogInterface.OnCancelListener? = null

    constructor(context: Context, layoutRes: Int) : this(
        context, LayoutInflater.from(context).inflate(layoutRes, null)
    )

    init {
        val activityContext = context.activity
        require(activityContext is FragmentActivity) { "the Context must be a FragmentActivity" }
        mActivity = activityContext
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDialog()
        return contentView
    }

    protected open fun initDialog() {
        //没有设置背景时，设置一个默认背景
        //设置了背景时，windowRadius、windowColor失效
        val backgroundDrawable = windowDrawable ?: GradientDrawable().apply {
            cornerRadius = windowRadius
            color = ColorStateList.valueOf(windowColor)
        }
        dialog?.setCanceledOnTouchOutside(canceledOnTouchOutside)
        dialog?.setCancelable(cancellable)
        dialog?.window?.apply {
            decorView.setPadding(0)
            setBackgroundDrawable(backgroundDrawable)
            setElevation(windowElevation)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && contentBlurRadius > 0) {
                setBackgroundBlurRadius(contentBlurRadius)
            }
            setWindowAttrs(this)
            //设置进出场动画
            windowAnimate?.let {
                setWindowAnimations(getWindowAnimate(it))
            }
        }
    }

    protected open fun getWindowAnimate(animate: Int): Int {
        return when (animate) {
            WindowAni.START -> R.style.mc_slideFromStart
            WindowAni.TOP -> R.style.mc_slideFromTop
            WindowAni.END -> R.style.mc_slideFromEnd
            WindowAni.BOTTOM -> R.style.mc_slideFromBottom
            WindowAni.CENTER -> R.style.mc_scaleFromCenter
            else -> animate
        }
    }

    protected open fun setWindowAttrs(window: Window) {
        val attributes = window.attributes
        //根据横竖屏不同，设置不同的宽度
        if (windowWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            val widthScale = if (isPortrait) 0.85f else 0.6f
            attributes.width = (screenWidth * widthScale).toInt()
        } else {
            attributes.width = windowWidth
        }
        if (windowHeight != ViewGroup.LayoutParams.WRAP_CONTENT) {
            attributes.height = windowHeight
        }
        attributes.x = xOffset
        attributes.y = yOffset
        attributes.dimAmount = windowAmount
        attributes.gravity = windowGravity
        //设置背景高斯模糊
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && windowBehindBlurRadius > 0) {
            attributes.flags = FLAG_BLUR_BEHIND
            attributes.blurBehindRadius = windowBehindBlurRadius
        }
        window.attributes = attributes
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(requireDialog())
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        super.onCancel(dialog)
        cancelListener?.onCancel(requireDialog())
    }

    override fun getTheme(): Int {
        return dialogTheme
    }

    fun setTheme(@StyleRes dialogTheme: Int) = apply {
        this.dialogTheme = dialogTheme
    }

    fun setCanceledOnTouchOutside(canceled: Boolean) = apply {
        canceledOnTouchOutside = canceled
    }

    fun setCancellable(cancelable: Boolean) = apply {
        cancellable = cancelable
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) = apply {
        dismissListener = listener
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) = apply {
        cancelListener = listener
    }

    fun setWindowColor(@ColorInt color: Int) = apply {
        windowColor = color
    }

    fun setWindowColor(color: String) = apply {
        windowColor = Color.parseColor(color)
    }

    fun setRadius(radius: Float) = apply {
        windowRadius = radius
    }

    fun setBackground(drawable: Drawable?) = apply {
        windowDrawable = drawable
    }

    fun setAmount(amount: Float) = apply {
        windowAmount = amount
    }

    fun setWidth(width: Int) = apply {
        windowWidth = width
    }

    fun setHeight(height: Int) = apply {
        windowHeight = height
    }

    fun setGravity(gravity: Int) = apply {
        windowGravity = gravity
    }

    /**
     * 设置dialog的进出场动画，可以是[WindowAni]或者[androidx.annotation.StyleRes]
     * @param animate Int?
     */
    fun setAnimate(animate: Int?) = apply {
        windowAnimate = animate
    }

    fun setXOffset(offset: Int) = apply {
        xOffset = offset
    }

    fun setYOffset(offset: Int) = apply {
        yOffset = offset
    }

    fun setWindowBehindBlur(radius: Int) = apply {
        windowBehindBlurRadius = radius
    }

    /**
     * 高斯模糊生效需要设置[android.R.attr.windowIsTranslucent]、[android.R.attr.windowIsFloating]
     * see [Window.setBackgroundBlurRadius]
     * @param radius Int
     * @param theme Int
     * @return BaseDialog
     */
    fun setContentBlur(radius: Int, @StyleRes theme: Int = R.style.TranslucentDialog) = apply {
        contentBlurRadius = radius
        dialogTheme = theme
    }

    fun setElevation(elevation: Float) = apply {
        windowElevation = elevation
    }

    open fun show(tag: String? = "default") = apply {
        show(mActivity.supportFragmentManager, tag)
    }

    open fun showNow(tag: String? = "default") = apply {
        showNow(mActivity.supportFragmentManager, tag)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isMainThread()) {
            showAllowingStateLoss(manager, tag) { commitAllowingStateLoss() }
        } else {
            requireActivity().runOnUiThread {
                showAllowingStateLoss(manager, tag) { commitAllowingStateLoss() }
            }
        }
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (isMainThread()) {
            showAllowingStateLoss(manager, tag) { commitNowAllowingStateLoss() }
        } else {
            requireActivity().runOnUiThread {
                showAllowingStateLoss(manager, tag) { commitNowAllowingStateLoss() }
            }
        }
    }

    private fun showAllowingStateLoss(
        manager: FragmentManager,
        tag: String?,
        action: FragmentTransaction.() -> Unit
    ) {
        try {
            val dismissed = DialogFragment::class.java.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            dismissed.set(this, false)

            val shown = DialogFragment::class.java.getDeclaredField("mShownByMe")
            shown.isAccessible = true
            shown.set(this, true)

            val ft = manager.beginTransaction()
            ft.add(this, tag)
            action.invoke(ft)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}