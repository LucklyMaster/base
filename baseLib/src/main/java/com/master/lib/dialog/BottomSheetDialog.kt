package com.master.lib.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.master.lib.R
import com.master.lib.enums.SheetState
import com.master.lib.ext.screenHeight
import com.master.lib.ext.screenWidth
import com.master.lib.view.SheetLayout

/**
 * 底部抽屉Dialog
 * @author: MasterChan
 * @date: 2022-09-07 21:03
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BottomSheetDialog(context: Context, contentView: View? = null) :
    BaseDialog(context, contentView) {

    override var windowWidth = context.screenWidth
    override var windowColor = Color.TRANSPARENT
    override var windowGravity = Gravity.BOTTOM

    var sheetLayout: SheetLayout? = null

    var enableDrag = true
    var enableFoldModel = true
    var expandHeight = context.screenHeight / 2
    var expandHeightRatio = 0.65f
    var displayHeight = expandHeight / 2
    var peekHeight = 0
    var curState = SheetState.FOLD
    var animatorSpeed = 950f / 300
    var fromBottomWithAnimator = true
    var onStateChangedListener: SheetLayout.OnStateChangedListener? = null
    var onScrollListener: SheetLayout.OnScrollListener? = null

    protected open val onFoldDismissListener = SheetLayout.OnStateChangedListener {
        if (it == SheetState.FOLD) dismiss()
    }

    constructor(context: Context, layoutRes: Int) : this(
        context, LayoutInflater.from(context).inflate(layoutRes, null)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initDialog()
        val root = layoutInflater.inflate(R.layout.mc_layout_bottom_sheet, null) as ViewGroup
        sheetLayout = root.findViewById(R.id.sheetLayout)
        initSheetLayout()
        if (contentView != null) {
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
            .setExpandHeightRatio(expandHeightRatio)
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

    open fun setState(state: SheetState) = apply {
        this.curState = state
    }

    open fun setPeekHeight(peekHeight: Int) = apply {
        this.peekHeight = peekHeight
    }

    open fun setExpandHeight(expandHeight: Int) = apply {
        this.expandHeight = expandHeight
    }

    open fun setExpandHeightRatio(expandHeightRatio: Float) = apply {
        this.expandHeightRatio = expandHeightRatio
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