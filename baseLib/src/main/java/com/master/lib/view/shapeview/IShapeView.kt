package com.master.lib.view.shapeview

import android.view.View
import androidx.annotation.ColorInt

/**
 * ShapeView定义接口
 * @author: MasterChan
 * @date: 2022-06-29 22:30
 */
interface IShapeView {

    fun bindView(view: View): IShapeView

    fun setUseRipple(useRipple: Boolean): IShapeView

    fun setStrokeWidth(stokeWidth: Int): IShapeView

    fun setStrokeColor(@ColorInt stokeColor: Int): IShapeView

    fun setNormalColor(@ColorInt normalColor: Int): IShapeView

    fun setPressedColor(@ColorInt pressedColor: Int): IShapeView

    fun setDisableColor(@ColorInt disableColor: Int): IShapeView

    fun setRippleColor(@ColorInt rippleColor: Int): IShapeView

    fun setLeftTopRadius(radius: Float): IShapeView

    fun setLeftBottomRadius(radius: Float): IShapeView

    fun setRightTopRadius(radius: Float): IShapeView

    fun setRightBottomRadius(radius: Float): IShapeView

    fun setRadius(radius: Float): IShapeView

    fun setCircle(isCircle: Boolean): IShapeView

    fun into()

    fun into(view: View)
}