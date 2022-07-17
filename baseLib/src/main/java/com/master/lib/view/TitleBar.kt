package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintSet
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

    override fun initView() {
        super.initView()
        val layoutParams = middleItem.layoutParams
        layoutParams.width = LayoutParams.WRAP_CONTENT
        middleItem.layoutParams = layoutParams

        val sets = ConstraintSet()
        val parentId = ConstraintSet.PARENT_ID
        sets.clone(this)

        sets.connect(middleItem.id, ConstraintSet.START, parentId, ConstraintSet.START)
        sets.connect(middleItem.id, ConstraintSet.END, parentId, ConstraintSet.END)

        sets.applyTo(this)
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
}