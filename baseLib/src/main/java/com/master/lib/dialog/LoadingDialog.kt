package com.master.lib.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.master.lib.R
import com.master.lib.ext.dp2pxi

/**
 * LoadingDialog
 * @author: MasterChan
 * @date: 2022-08-04 21:30
 */
class LoadingDialog : BaseDialog() {

    override var windowWidth = dp2pxi(160)
    override var windowHeight = dp2pxi(160)

    private val loadingView: ProgressBar by lazy { contentView!!.findViewById(R.id.pb_loading) }
    private val textView: TextView by lazy { contentView!!.findViewById(R.id.tv_text) }

    init {
        setContentView(R.layout.mc_dialog_loading)
    }

    fun setText(@StringRes textRes: Int) = apply {
        textView.setText(textRes)
    }

    fun setText(text: CharSequence) = apply {
        textView.text = text
    }

    fun setTextColor(textColor: ColorStateList?) = apply {
        textView.setTextColor(textColor)
    }

    fun setTextColor(@ColorInt textColor: Int) = apply {
        textView.setTextColor(textColor)
    }

    fun setTextColor(textColor: String) = apply {
        textView.setTextColor(Color.parseColor(textColor))
    }

    fun setTextSize(textSize: Float, unit: Int = TypedValue.COMPLEX_UNIT_SP) = apply {
        textView.setTextSize(unit, textSize)
    }

    fun setLoadingDrawable(drawable: Drawable?) = apply {
        loadingView.progressDrawable = drawable
    }

    fun setLoadingColor(color: Int) = apply {
        setLoadingColor(ColorStateList.valueOf(color))
    }

    fun setLoadingColor(color: ColorStateList?) = apply {
        if (loadingView.progressDrawable == null) {
            loadingView.progressDrawable = loadingView.indeterminateDrawable
        }
        loadingView.progressTintList = color
    }

}