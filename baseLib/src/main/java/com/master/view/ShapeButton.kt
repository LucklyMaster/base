package com.master.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/**
 * ShapeButton
 * @author: MasterChan
 * @date: 2022-07-02 01:00
 */
class ShapeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr), IShapeView {

    override val shapeHelper: ShapeViewHelper by lazy { ShapeViewHelper(this, attrs) }

    init {
        shapeHelper.into()
    }
}