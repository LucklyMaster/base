package com.master.lib.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.master.lib.ext.isPortrait
import com.master.lib.ext.screenWidth
import com.masterchan.lib.R

/**
 * BaseDialog
 * @author MasterChan
 * @date 2021-12-14 14:30
 */
open class BaseDialog(private var contentView: View? = null) : DialogFragment() {

    protected var windowDrawable: Drawable? = null
    protected var windowColor = Color.WHITE
    protected var windowRadius = 6f
    protected var windowAmount = 0.6f
    protected var windowWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    protected var windowHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    protected var windowGravity = Gravity.CENTER
    protected var windowAnimate: Int? = null
    protected var xOffset = 0
    protected var yOffset = 0
    protected var canceledOnTouchOutside = true
    protected var dismissListener: DialogInterface.OnDismissListener? = null
    protected var cancelListener: DialogInterface.OnCancelListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //没有背景时，默认设置一个白色的背景
        if (windowDrawable == null) {
            windowDrawable = GradientDrawable()
            (windowDrawable as GradientDrawable).cornerRadius = windowRadius
            (windowDrawable as GradientDrawable).color = ColorStateList.valueOf(windowColor)
        }
        dialog?.window?.decorView?.setPadding(0)
        dialog?.window?.setBackgroundDrawable(windowDrawable)
        dialog?.window?.setDimAmount(windowAmount)
        dialog?.window?.setGravity(windowGravity)
        dialog?.setCanceledOnTouchOutside(canceledOnTouchOutside)
        //设置进出场动画
        if (windowAnimate != null) {
            windowAnimate = when (windowAnimate) {
                WindowAni.START -> R.style.mc_fromStart
                WindowAni.TOP -> R.style.mc_fromTop
                WindowAni.END -> R.style.mc_fromEnd
                WindowAni.BOTTOM -> R.style.mc_fromBottom
                WindowAni.CENTER -> R.style.mc_fromCenter
                else -> windowAnimate
            }
            dialog?.window?.setWindowAnimations(windowAnimate!!)
        }

        val attributes = dialog?.window?.attributes
        //根据横竖屏不同，设置不同的宽度
        if (windowWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            val widthScale = if (isPortrait) 0.85f else 0.6f
            attributes?.width = (screenWidth * widthScale).toInt()
        } else {
            attributes?.width = windowWidth
        }
        if (windowHeight != ViewGroup.LayoutParams.WRAP_CONTENT) {
            attributes?.height = windowHeight
        }
        attributes?.x = xOffset
        attributes?.y = yOffset
        dialog?.window?.attributes = attributes
        return contentView
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(requireDialog())
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        super.onCancel(dialog)
        cancelListener?.onCancel(requireDialog())
    }

    /**
     * 当设置为true时，会导致[mCancelable]为false失效
     * @param canceled Boolean
     */
    fun setCanceledOnTouchOutside(canceled: Boolean) = apply {
        canceledOnTouchOutside = canceled
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

    fun setWindowRadius(radius: Float) = apply {
        windowRadius = radius
    }

    fun setWindowDrawable(drawable: Drawable?) = apply {
        windowDrawable = drawable
    }

    fun setWindowAmount(amount: Float) = apply {
        windowAmount = amount
    }

    fun setWindowWidth(width: Int) = apply {
        windowWidth = width
    }

    fun setWindowHeight(height: Int) = apply {
        windowHeight = height
    }

    fun setWindowGravity(gravity: Int) = apply {
        windowGravity = gravity
    }

    /**
     * 设置dialog的进出场动画，可以是[WindowAni]或者[androidx.annotation.StyleRes]
     * @param animate Int?
     */
    fun setWindowAnimate(animate: Int?) = apply {
        windowAnimate = animate
    }

    fun setXOffset(offset: Int) = apply {
        xOffset = offset
    }

    fun setYOffset(offset: Int) = apply {
        yOffset = offset
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return super.show(transaction, tag)
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        super.showNow(manager, tag)
    }

    fun showAllowingStateLoss(manager: FragmentManager, tag: String?) {
        try {
            val dismissed = DialogFragment::class.java.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            dismissed.set(this, false)

            val shown = DialogFragment::class.java.getDeclaredField("mShownByMe")
            shown.isAccessible = true
            shown.set(this, true)

            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}