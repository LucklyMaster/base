package com.masterchan.lib.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import com.masterchan.lib.R
import com.masterchan.lib.databinding.McDialogLoadingBinding
import com.masterchan.lib.ext.dp2px
import com.masterchan.lib.ext.isMainThread
import com.masterchan.lib.ext.toActivity

/**
 * LoadingDialog
 * @author MasterChan
 * @date 2021-12-16 14:35
 */
class LoadingDialog(
    context: Context,
    text: String? = "",
    cancelable: Boolean = true,
    styleRes: Int = R.style.mc_LoadingDialog
) {

    private val mBinding = McDialogLoadingBinding.bind(
        View.inflate(context, R.layout.mc_dialog_loading, null)
    )
    private val mDialogFragment: BaseDialog = BaseDialog(mBinding.root)
    private var mText = text
    private var mCancelable = cancelable
    val context: FragmentActivity

    init {
        val activity = context.toActivity() ?: throw Exception("the Context not attach a Activity")
        if (activity !is FragmentActivity) {
            throw Exception("the Context must be a FragmentActivity")
        }
        this.context = activity

        val a = context.obtainStyledAttributes(
            null, R.styleable.LoadingDialog, R.attr.mc_LoadingDialogDefaultStyle, styleRes
        )
        if (mText.isNullOrEmpty()) {
            mText = a.getString(R.styleable.LoadingDialog_mc_text) ?: mText
        }
        val textSize = a.getDimension(R.styleable.LoadingDialog_mc_textSize, dp2px(16f))
        val textColor = a.getColor(R.styleable.LoadingDialog_mc_textColor, Color.BLACK)
        val progressColor = a.getColorStateList(R.styleable.LoadingDialog_mc_progressColor)
        val progressDrawable = a.getDrawable(R.styleable.LoadingDialog_mc_progressDrawable)
        val windowColor = a.getColor(R.styleable.LoadingDialog_mc_windowColor, Color.WHITE)
        val windowRadius = a.getDimension(R.styleable.LoadingDialog_mc_windowRadius, dp2px(4f))
        val windowDrawable = a.getDrawable(R.styleable.LoadingDialog_mc_windowDrawable)
        val windowAmount = a.getFloat(R.styleable.LoadingDialog_mc_windowAmount, 0.5f)
        val windowWidth = a.getDimension(R.styleable.LoadingDialog_mc_windowWidth, dp2px(160f))
        val windowHeight = a.getDimension(R.styleable.LoadingDialog_mc_windowHeight, windowWidth)
        a.recycle()

        with(mBinding) {
            if (mText.isNullOrEmpty()) {
                tvText.visibility = View.GONE
            } else {
                tvText.text = mText
            }
            tvText.setTextColor(textColor)
            tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            progressDrawable?.let { pbLoading.progressDrawable = it }
            progressColor?.let {
                if (pbLoading.progressDrawable == null) {
                    pbLoading.progressDrawable = pbLoading.indeterminateDrawable
                }
                pbLoading.progressTintList = it
            }
        }
        mDialogFragment.setWindowAmount(windowAmount)
        mDialogFragment.setWindowColor(windowColor)
        mDialogFragment.setWindowDrawable(windowDrawable)
        mDialogFragment.setWindowRadius(windowRadius)
        mDialogFragment.setWindowWidth(windowWidth.toInt())
        mDialogFragment.setWindowHeight(windowHeight.toInt())
        mDialogFragment.isCancelable = mCancelable
        if (!mCancelable) {
            mDialogFragment.setCanceledOnTouchOutside(false)
        }
    }

    fun setText(text: String?): LoadingDialog {
        if (text.isNullOrEmpty()) {
            mBinding.tvText.visibility = View.GONE
        } else {
            mBinding.tvText.visibility = View.VISIBLE
            mBinding.tvText.text = text
        }
        return this
    }

    /**
     * 设置[cancelable]为false时，如果[setCanceledOnTouchOutside]为true会导致其失效，所以当[cancelable]
     * 为false时，手动设置[setCanceledOnTouchOutside]为false
     * @param cancelable Boolean
     * @return Builder
     */
    fun setCancelable(cancelable: Boolean): LoadingDialog {
        mDialogFragment.isCancelable = cancelable
        return this
    }

    fun setCanceledOnTouchOutside(canceled: Boolean): LoadingDialog {
        mDialogFragment.setCanceledOnTouchOutside(canceled)
        return this
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?): LoadingDialog {
        mDialogFragment.setOnDismissListener(listener)
        return this
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?): LoadingDialog {
        mDialogFragment.setOnCancelListener(listener)
        return this
    }

    fun show(tag: String = "default"): LoadingDialog {
        mDialogFragment.showNow(context.supportFragmentManager, tag)
        return this
    }

    fun safeShow(tag: String = "default") {
        if (isMainThread()) {
            mDialogFragment.showNow(context.supportFragmentManager, tag)
        } else {
            context.runOnUiThread { mDialogFragment.showNow(context.supportFragmentManager, tag) }
        }
    }

    fun dismiss() {
        if (isMainThread()) {
            mDialogFragment.dismissAllowingStateLoss()
        } else {
            context.runOnUiThread { mDialogFragment.dismissAllowingStateLoss() }
        }
    }

    class Builder(private val context: Context) {
        private var mText: String? = null
        private var mTextColor: Int? = null
        private var mTextSize: Float? = null
        private var mProgressColor: Int? = null
        private var mProgressDrawable: Drawable? = null
        private var mCancelable = true
        private var mCanceledOnTouchOutside = true
        private var mWindowDrawable: Drawable? = null
        private var mWindowColor: Int? = null
        private var mWindowRadius: Float? = null
        private var mWindowAmount: Float? = null
        private var mWindowWidth: Int? = null
        private var mWindowHeight: Int? = null
        private var mOnCancelListener: DialogInterface.OnCancelListener? = null
        private var mOnDismissListener: DialogInterface.OnDismissListener? = null

        fun setText(text: String?): Builder {
            mText = text
            return this
        }

        fun setTextColor(@ColorInt color: Int?): Builder {
            mTextColor = color
            return this
        }

        fun setTextSize(textSize: Float?): Builder {
            mTextSize = textSize
            return this
        }

        fun setProgressColor(@ColorInt color: Int?): Builder {
            mProgressColor = color
            return this
        }

        fun setProgressDrawable(@DrawableRes drawable: Int): Builder {
            mProgressDrawable = AppCompatResources.getDrawable(context, drawable)
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            mCancelable = cancelable
            return this
        }

        fun setCanceledOnTouchOutside(canceled: Boolean): Builder {
            mCanceledOnTouchOutside = canceled
            return this
        }

        fun setWindowAmount(amount: Float): Builder {
            mWindowAmount = amount
            return this
        }

        fun setWindowColor(@ColorInt color: Int): Builder {
            mWindowColor = color
            return this
        }

        fun setWindowDrawable(@DrawableRes drawable: Int): Builder {
            mWindowDrawable = AppCompatResources.getDrawable(context, drawable)
            return this
        }

        fun setWindowRadius(radius: Float): Builder {
            mWindowRadius = radius
            return this
        }

        fun setWindowDrawable(drawable: Drawable): Builder {
            mWindowDrawable = drawable
            return this
        }

        fun setWindowWidth(width: Int): Builder {
            mWindowWidth = width
            return this
        }

        fun setWindowHeight(height: Int): Builder {
            mWindowHeight = height
            return this
        }

        fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
            mOnDismissListener = listener
        }

        fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
            mOnCancelListener = listener
        }

        fun create(): LoadingDialog {
            val dialog = LoadingDialog(context)
            with(dialog.mBinding) {
                if (mText.isNullOrEmpty()) {
                    tvText.visibility = View.GONE
                } else {
                    tvText.text = mText
                }
                mTextColor?.let { tvText.setTextColor(it) }
                mTextSize?.let { tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
                mProgressColor?.let { pbLoading.progressTintList = ColorStateList.valueOf(it) }
                mProgressDrawable?.let { pbLoading.progressDrawable = it }
            }
            mWindowAmount?.let { dialog.mDialogFragment.setWindowAmount(it) }
            mWindowColor?.let { dialog.mDialogFragment.setWindowColor(it) }
            mWindowDrawable?.let { dialog.mDialogFragment.setWindowDrawable(it) }
            mWindowRadius?.let { dialog.mDialogFragment.setWindowRadius(it) }
            mWindowWidth?.let { dialog.mDialogFragment.setWindowWidth(it) }
            mWindowHeight?.let { dialog.mDialogFragment.setWindowHeight(it) }
            return dialog.setCancelable(mCancelable)
                .setCanceledOnTouchOutside(mCanceledOnTouchOutside)
                .setOnCancelListener(mOnCancelListener)
                .setOnDismissListener(mOnDismissListener)
        }

        fun show(): LoadingDialog {
            val dialog = create()
            dialog.show()
            return dialog
        }
    }
}