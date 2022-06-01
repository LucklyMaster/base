package com.masterchan.lib.widget

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * @author MasterChan
 * @date 2021-12-29 10:08
 * @describe ViewHolder
 */
open class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun getImageView(@IdRes id: Int): ImageView? {
        return getView(id)
    }

    fun getTextView(@IdRes id: Int): TextView? {
        return getView(id)
    }

    fun getButton(@IdRes id: Int): Button? {
        return getView(id)
    }

    fun <T : View> getView(@IdRes id: Int): T? {
        return itemView.findViewById(id)
    }
}