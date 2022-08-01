package com.master.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * ShapeFrameLayout
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IShapeView {

    override val shapeHelper: ShapeViewHelper by lazy { ShapeViewHelper(this, attrs) }

    init {
        shapeHelper.into()
    }
}