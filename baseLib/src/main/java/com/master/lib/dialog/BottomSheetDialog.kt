package com.master.lib.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.master.lib.R
import com.master.lib.ext.screenHeight
import com.master.lib.ext.screenWidth

/**
 * 底部抽屉Dialog
 * @author: MasterChan
 * @date: 2022-09-07 21:03
 */
class BottomSheetDialog(context: Context) : BaseDialog(context, R.layout.mc_dialog_bottom_sheet) {

    override var windowWidth = context.screenWidth
    override var windowHeight = context.screenHeight
    override var windowColor = Color.TRANSPARENT

    init {
        windowGravity = Gravity.BOTTOM
        // windowHeight = context.screenHeight
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentView!!.findViewById<View>(R.id.mask).apply {
            // (0.1 * 0xFF).toInt() shl 24
            val value = 0.6f
            setBackgroundColor(argb(0f, value, value, value))
        }.setOnClickListener { dismiss() }
    }

    fun argb(alpha: Float, red: Float, green: Float, blue: Float): Int {
        return (alpha * 255.0f + 0.5f).toInt() shl 24 or
                ((red * 255.0f + 0.5f).toInt() shl 16) or
                ((green * 255.0f + 0.5f).toInt() shl 8) or
                (blue * 255.0f + 0.5f).toInt()
    }
}