package com.master.lib.view

import android.content.Context
import android.util.AttributeSet

/**
 * ShapeCellView
 * @author: MasterChan
 * @date: 2022-09-18 10:22
 */
open class ShapeCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : CellView(context, attrs, defStyleAttr, defStyleRes), IShapeView {

    override val shapeHelper = ShapeViewHelper(this, attrs)

    init {
        shapeHelper.into()
    }
}