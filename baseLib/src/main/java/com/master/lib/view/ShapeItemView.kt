package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import com.masterchan.lib.R

/**
 * ShapeItemView
 * @author MasterChan
 * @date 2021-12-13 15:00
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_shapeItemViewDefaultStyle,
    defStyleRes: Int = R.style.mc_ShapeItemView
) : TitleBar(context, attrs, defStyleAttr, defStyleRes), IShapeView {

    override val shapeHelper: ShapeViewHelper by lazy { ShapeViewHelper(this, attrs, defStyleAttr) }

    init {
        shapeHelper.into()
    }
}