package com.master.lib.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * ShapeConstraintLayout
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
open class ShapeConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), IShapeView {

    override val shapeHelper = ShapeViewHelper(this, attrs)

    init {
        shapeHelper.into()
    }
}