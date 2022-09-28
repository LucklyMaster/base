package com.master.lib.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.master.lib.R
import com.master.lib.enums.SheetState
import com.master.lib.ext.application
import com.master.lib.ext.screenWidth
import com.master.lib.view.SheetLayout

/**
 * 底部抽屉Dialog
 * @author: MasterChan
 * @date: 2022-09-07 21:03
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BottomSheetDialog : BaseDialog() {

    override var windowWidth = application.screenWidth
    override var windowColor = Color.TRANSPARENT
    override var windowGravity = Gravity.BOTTOM

    var sheetLayout: SheetLayout? = null
    private var layoutRes = 0

    var enableDrag = true
    var enableFoldModel = true
    var expandHeight = 0
    var displayHeight = 0
    var peekHeight = 0
    var curState = SheetState.FOLD
    var animatorSpeed = 950f / 300
    var fromBottomWithAnimator = true
    var onStateChangedListener: SheetLayout.OnStateChangedListener? = null
    var onScrollListener: SheetLayout.OnScrollListener? = null

    protected open val onFoldDismissListener = SheetLayout.OnStateChangedListener {
        if (it == SheetState.FOLD) dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initDialog()
        val root = layoutInflater.inflate(R.layout.mc_layout_bottom_sheet, null) as ViewGroup
        sheetLayout = root.findViewById(R.id.sheetLayout)
        initSheetLayout()
        if (layoutRes != 0) {
            LayoutInflater.from(requireContext()).inflate(layoutRes, sheetLayout)
        } else if (contentView != null) {
            sheetLayout!!.addView(contentView)
        }
        setContentView(root)
        return contentView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentView!!.findViewById<View>(R.id.mask).setOnClickListener { dismiss() }
    }

    protected open fun initSheetLayout() {
        sheetLayout!!.enableDrag(enableDrag)
            .enableFoldModel(enableFoldModel)
            .setPeekHeight(peekHeight)
            .setDisplayHeight(displayHeight)
            .setExpandHeight(expandHeight)
            .setAnimatorSpeed(animatorSpeed)
            .addOnStateChangedListener(onFoldDismissListener)
        if (fromBottomWithAnimator) {
            sheetLayout!!.post { sheetLayout!!.setState(curState) }
        } else {
            sheetLayout!!.setState(curState)
        }
        onStateChangedListener?.let { sheetLayout!!.addOnStateChangedListener(it) }
        onScrollListener?.let { sheetLayout!!.addOnScrollListener(it) }
    }

    override fun setContentView(layoutRes: Int) = apply {
        this.layoutRes = layoutRes
    }

    open fun setState(state: SheetState) = apply {
        this.curState = state
    }

    open fun setPeekHeight(peekHeight: Int) = apply {
        this.peekHeight = peekHeight
    }

    open fun setExpandHeight(expandHeight: Int) = apply {
        this.expandHeight = expandHeight
    }

    open fun setDisplayHeight(displayHeight: Int) = apply {
        this.displayHeight = displayHeight
    }

    open fun enableDrag(enableDrag: Boolean) = apply {
        this.enableDrag = enableDrag
    }

    open fun enableFoldModel(enableFoldModel: Boolean) = apply {
        this.enableFoldModel = enableFoldModel
    }

    open fun setAnimatorSpeed(animatorSpeed: Float) = apply {
        this.animatorSpeed = animatorSpeed
    }

    open fun isFromBottomWithAnimator(fromBottomWithAnimator: Boolean) = apply {
        this.fromBottomWithAnimator = fromBottomWithAnimator
    }

    open fun addOnStateChangedListener(listener: SheetLayout.OnStateChangedListener) = apply {
        this.onStateChangedListener = listener
    }

    open fun addOnScrollListener(listener: SheetLayout.OnScrollListener) = apply {
        this.onScrollListener = listener
    }
}