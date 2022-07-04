package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * ShapeTextView
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IShapeView {

    override val shapeHelper: ShapeViewHelper by lazy { ShapeViewHelper(this, attrs) }

    init {
        shapeHelper.into()
    }
}