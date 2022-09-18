package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import com.master.lib.R

/**
 * ShapeItemView
 * @author: MasterChan
 * @date: 2022-09-18 10:48
 */
open class ShapeItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.mc_itemViewDefaultStyle,
    defStyleRes: Int = R.style.mc_ItemView
) : ItemView(context, attrs, defStyleAttr, defStyleRes), IShapeView {

    override val shapeHelper = ShapeViewHelper(this, attrs)

    init {
        shapeHelper.into()
    }
}