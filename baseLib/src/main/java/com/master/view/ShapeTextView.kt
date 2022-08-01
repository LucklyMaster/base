package com.master.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * ShapeTextView
 * @author MasterChan
 * @date 2021-12-10 10:18
 */
@Suppress("MemberVisibilityCanBePrivate")
class ShapeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), IShapeView {

    override val shapeHelper: ShapeViewHelper by lazy { ShapeViewHelper(this, attrs) }

    init {
        shapeHelper.into()
    }
}