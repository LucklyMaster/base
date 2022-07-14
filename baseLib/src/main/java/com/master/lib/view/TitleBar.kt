package com.master.lib.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.WindowInsets
import com.master.lib.ext.activity
import com.masterchan.lib.R

/**
 * TitleBar
 * @author MasterChan
 * @date 2021-12-09 18:37
 */
@Suppress("MemberVisibilityCanBePrivate")
open class TitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_titleBarDefaultStyle,
    defStyleRes: Int = R.style.mc_TitleBar
) : ItemView(context, attrs, defStyleAttr, defStyleRes) {

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs, R.styleable.TitleBar, defStyleAttr, defStyleRes
        )
        setClickLeftFinish(a.getBoolean(R.styleable.TitleBar_mc_clickLeftFinish, true))
        a.recycle()
    }

    fun setClickLeftFinish(finish: Boolean) = apply {
        if (!finish) {
            leftItem.iconView.setOnClickListener(null)
            leftItem.labelView.setOnClickListener(null)
        }
        if (leftItem.icon != null) {
            leftItem.iconView.setOnClickListener { activity?.finish() }
        }
        if (leftItem.text.isNotEmpty()) {
            leftItem.labelView.setOnClickListener { activity?.finish() }
        }
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        if (fitsSystemWindows) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                insets?.inset(0, 0, 0, 0)
            }
            return WindowInsets(insets)
        }
        return super.dispatchApplyWindowInsets(insets)
    }
}