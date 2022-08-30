package com.master.lib.widget

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.master.lib.ext.setText

/**
 * ViewHolder
 * @author MasterChan
 * @date 2021-12-29 10:08
 */
open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun getImageView(@IdRes id: Int): ImageView? {
        return getView(id)
    }

    fun getTextView(@IdRes id: Int): TextView? {
        return getView(id)
    }

    fun getButton(@IdRes id: Int): Button? {
        return getView(id)
    }

    open fun setImage(@IdRes id: Int, @DrawableRes res: Int) = apply {
        getImageView(id)?.setImageResource(res)
    }

    open fun setImage(@IdRes id: Int, drawable: Drawable) = apply {
        getImageView(id)?.setImageDrawable(drawable)
    }

    open fun setImage(@IdRes id: Int, bitmap: Bitmap) = apply {
        getImageView(id)?.setImageBitmap(bitmap)
    }

    open fun setText(@IdRes id: Int, text: CharSequence) = apply {
        getTextView(id)?.text = text
    }

    open fun setText(@IdRes id: Int, @StringRes res: Int, vararg format: Any) = apply {
        getTextView(id)?.setText(res, format)
    }

    fun <T : View> getView(@IdRes id: Int): T? {
        return itemView.findViewById(id)
    }
}