package com.masterchan.lib.dialog

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
import com.masterchan.lib.R
import com.masterchan.lib.ext.isPortrait
import com.masterchan.lib.ext.screenWidth

/**
 * BaseDialog
 * @author MasterChan
 * @date 2021-12-14 14:30
 */
open class BaseDialog(private var contentView: View?) : DialogFragment() {

    constructor() : this(null)

    private var mWindowDrawable: Drawable? = null
    private var mWindowColor = Color.WHITE
    private var mWindowRadius = 12f
    private var mWindowAmount = 1f
    private var mWindowWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mWindowHeight = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mWindowGravity = Gravity.CENTER
    private var mWindowAnimate: Int? = null
    private var mXOffset = 0
    private var mYOffset = 0
    private var mCanceledOnTouchOutside = true
    private var mDismissListener: DialogInterface.OnDismissListener? = null
    private var mCancelListener: DialogInterface.OnCancelListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //没有背景时，默认设置一个白色的背景
        if (mWindowDrawable == null) {
            mWindowDrawable = GradientDrawable()
            (mWindowDrawable as GradientDrawable).cornerRadius = mWindowRadius
            (mWindowDrawable as GradientDrawable).color = ColorStateList.valueOf(mWindowColor)
        }
        dialog?.window?.decorView?.setPadding(0)
        dialog?.window?.setBackgroundDrawable(mWindowDrawable)
        dialog?.window?.setDimAmount(mWindowAmount)
        dialog?.window?.setGravity(mWindowGravity)
        dialog?.setCanceledOnTouchOutside(mCanceledOnTouchOutside)
        //设置进出场动画
        if (mWindowAnimate != null) {
            mWindowAnimate = when (mWindowAnimate) {
                WindowAni.START -> R.style.mc_fromStart
                WindowAni.TOP -> R.style.mc_fromTop
                WindowAni.END -> R.style.mc_fromEnd
                WindowAni.BOTTOM -> R.style.mc_fromBottom
                WindowAni.CENTER -> R.style.mc_fromCenter
                else -> mWindowAnimate
            }
            dialog?.window?.setWindowAnimations(mWindowAnimate!!)
        }

        val attributes = dialog?.window?.attributes
        //根据横竖屏不同，设置不同的宽度
        if (mWindowWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            val widthScale = if (requireContext().isPortrait) 0.85f else 0.6f
            attributes?.width = (requireContext().screenWidth * widthScale).toInt()
        } else {
            attributes?.width = mWindowWidth
        }
        if (mWindowHeight != ViewGroup.LayoutParams.WRAP_CONTENT) {
            attributes?.height = mWindowHeight
        }
        attributes?.x = mXOffset
        attributes?.y = mYOffset
        dialog?.window?.attributes = attributes
        return contentView
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        mDismissListener?.onDismiss(requireDialog())
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        super.onCancel(dialog)
        mCancelListener?.onCancel(requireDialog())
    }

    /**
     * 当设置为true时，会导致[mCancelable]为false失效
     * @param canceled Boolean
     */
    fun setCanceledOnTouchOutside(canceled: Boolean) = apply {
        mCanceledOnTouchOutside = canceled
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) = apply {
        mDismissListener = listener
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) = apply {
        mCancelListener = listener
    }

    fun setWindowColor(@ColorInt color: Int) = apply {
        mWindowColor = color
    }

    fun setWindowRadius(radius: Float) = apply {
        mWindowRadius = radius
    }

    fun setWindowDrawable(drawable: Drawable?) = apply {
        mWindowDrawable = drawable
    }

    fun setWindowAmount(amount: Float) = apply {
        mWindowAmount = amount
    }

    fun setWindowWidth(width: Int) = apply {
        mWindowWidth = width
    }

    fun setWindowHeight(height: Int) = apply {
        mWindowHeight = height
    }

    fun setWindowGravity(gravity: Int) = apply {
        mWindowGravity = gravity
    }

    /**
     * 设置dialog的进出场动画，可以是[WindowAni]或者[androidx.annotation.StyleRes]
     * @param animate Int?
     */
    fun setWindowAnimate(animate: Int?) = apply {
        mWindowAnimate = animate
    }

    fun setXOffset(offset: Int) = apply {
        mXOffset = offset
    }

    fun setYOffset(offset: Int) = apply {
        mYOffset = offset
    }
}