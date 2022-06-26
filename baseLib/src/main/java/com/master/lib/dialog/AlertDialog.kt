package com.master.lib.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.master.lib.ext.*
import com.masterchan.lib.R
import com.masterchan.lib.databinding.McDialogAlertBinding
import com.master.lib.widget.RecyclerViewDivider
import com.master.lib.widget.ViewHolder

/**
 * AlertDialog
 * @author MasterChan
 * @date 2021-12-14 10:51
 */
@Suppress("MemberVisibilityCanBePrivate")
class AlertDialog private constructor(context: Context) {

    private val binding: McDialogAlertBinding = McDialogAlertBinding.bind(
        View.inflate(context, R.layout.mc_dialog_alert, null)
    )
    val titleView = binding.tvTitle
    val messageView = binding.tvMessage
    val positiveButton = binding.btnPositive
    val negativeButton = binding.btnNegative
    val neutralButton = binding.btnNeutral
    val viewContainer = binding.viewContainer

    private val dialogFragment: BaseDialog = BaseDialog(binding.root)
    val activity: FragmentActivity

    init {
        val con = context.activity ?: throw Exception("the Context not attach a Activity")
        if (con !is FragmentActivity) {
            throw Exception("the Context must be a FragmentActivity")
        }
        this.activity = con
    }

    fun show(tag: String? = "default") {
        if (isMainThread()) {
            dialogFragment.showNow(activity.supportFragmentManager, tag)
        } else {
            activity.runOnUiThread { dialogFragment.showNow(activity.supportFragmentManager, tag) }
        }
    }

    fun dismiss() {
        if (isMainThread()) {
            dialogFragment.dismissAllowingStateLoss()
        } else {
            activity.runOnUiThread { dialogFragment.dismissAllowingStateLoss() }
        }
    }

    fun interface OnClickListener {
        fun onClick(dialog: AlertDialog)
    }

    fun interface OnItemClickListener {
        fun onItemClick(dialog: AlertDialog, view: View, which: Int)
    }

    fun interface OnMultiItemClickListener {
        fun onItemClick(dialog: AlertDialog, view: View, checked: Boolean, which: Int)
    }

    fun interface OnItemSelectedListener {
        fun onItemSelected(dialog: AlertDialog, view: CompoundButton, checkedItems: List<Int>)
    }

    fun interface IListItemData {
        fun getItemText(): String
    }

    class Builder(private val context: Context, styleRes: Int = R.style.mc_AlertDialog) {

        private var positiveTextSize: Float
        private var negativeTextSize: Float
        private var neutralTextSize: Float

        private var positiveTextColor = context.getColorStateList(R.color.mc_4A)
        private var negativeTextColor = context.getColorStateList(R.color.mc_4A)
        private var neutralTextColor = context.getColorStateList(R.color.mc_4A)
        private var titleTextColor = context.getColorStateList(R.color.mc_32)
        private var messageTextColor = context.getColorStateList(R.color.mc_32)

        private var titleGravity: Int
        private var titleTextSize: Float
        private var messageTextSize: Float

        private var titleText: CharSequence? = null
        private var messageText: CharSequence? = null
        private var positiveText: CharSequence? = null
        private var negativeText: CharSequence? = null
        private var neutralText: CharSequence? = null

        private var positiveBackground: Drawable? = null
        private var negativeBackground: Drawable? = null
        private var neutralBackground: Drawable? = null

        private var listItemRes: Int
        private var listTextColor = context.getColorStateList(R.color.mc_32)
        private var listTextSize: Float
        private var listDividerVisible: Boolean
        private var listDividerHeight: Float
        private var listDividerColor: Int
        private var listAdapter: Adapter? = null

        private var positiveClickListener: OnClickListener? = null
        private var negativeClickListener: OnClickListener? = null
        private var neutralClickListener: OnClickListener? = null
        private var onItemSelectedListener: OnItemSelectedListener? = null
        private var onCancelListener: DialogInterface.OnCancelListener? = null
        private var onDismissListener: DialogInterface.OnDismissListener? = null

        private var customView: View? = null
        private var cancelable = true
        private var canceledOnTouchOutside = true
        private var viewLayoutRes = 0
        private var viewLayoutParams: FrameLayout.LayoutParams? = null
        private var windowGravity = Gravity.CENTER
        private var windowAmount = 1f
        private var windowAnimation: Int? = null
        private var xOffset = 0
        private var yOffset = 0
        private var windowColor = Color.WHITE
        private var windowDrawable: Drawable? = null
        private var windowRadius: Float = dp2px(6f)
        private var windowWidth = ViewGroup.LayoutParams.WRAP_CONTENT
        private var windowHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        init {
            val a = context.theme.obtainStyledAttributes(
                null, R.styleable.AlertDialog, R.attr.mc_AlertDialogDefaultStyle,
                styleRes
            )

            //button textSize
            val btnTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_btnTextSize, dp2px(16f)
            )
            positiveTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_positiveTextSize, btnTextSize
            )
            negativeTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_negativeTextSize, btnTextSize
            )
            neutralTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_neutralTextSize, btnTextSize
            )

            //button textColor
            if (a.hasValue(R.styleable.AlertDialog_mc_btnTextColor)) {
                positiveTextColor = a.getColorStateList(R.styleable.AlertDialog_mc_btnTextColor)!!
                negativeTextColor = a.getColorStateList(R.styleable.AlertDialog_mc_btnTextColor)!!
                neutralTextColor = a.getColorStateList(R.styleable.AlertDialog_mc_btnTextColor)!!
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_positiveTextColor)) {
                positiveTextColor = a.getColorStateList(
                    R.styleable.AlertDialog_mc_positiveTextColor
                )!!
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_negativeTextColor)) {
                positiveTextColor = a.getColorStateList(
                    R.styleable.AlertDialog_mc_negativeTextColor
                )!!
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_neutralTextColor)) {
                neutralTextColor = a.getColorStateList(
                    R.styleable.AlertDialog_mc_neutralTextColor
                )!!
            }

            //button text
            if (a.hasValue(R.styleable.AlertDialog_mc_positiveText)) {
                positiveText = a.getString(R.styleable.AlertDialog_mc_positiveText)
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_negativeText)) {
                negativeText = a.getString(R.styleable.AlertDialog_mc_negativeText)
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_neutralText)) {
                neutralText = a.getString(R.styleable.AlertDialog_mc_neutralText)
            }

            //btn background
            if (a.hasValue(R.styleable.AlertDialog_mc_btnBackground)) {
                positiveBackground = a.getDrawable(R.styleable.AlertDialog_mc_btnBackground)
                negativeBackground = a.getDrawable(R.styleable.AlertDialog_mc_btnBackground)
                neutralBackground = a.getDrawable(R.styleable.AlertDialog_mc_btnBackground)
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_positiveBackground)) {
                positiveBackground = a.getDrawable(R.styleable.AlertDialog_mc_positiveBackground)
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_negativeBackground)) {
                negativeBackground = a.getDrawable(R.styleable.AlertDialog_mc_negativeBackground)
            }
            if (a.hasValue(R.styleable.AlertDialog_mc_neutralBackground)) {
                neutralBackground = a.getDrawable(R.styleable.AlertDialog_mc_neutralBackground)
            }

            //title
            titleGravity = getGravity(a.getInt(R.styleable.AlertDialog_mc_titleGravity, 0))
            titleTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_titleTextSize, dp2px(18f)
            )
            if (a.hasValue(R.styleable.AlertDialog_mc_titleTextColor)) {
                titleTextColor = a.getColorStateList(R.styleable.AlertDialog_mc_titleTextColor)!!
            }

            //message
            messageTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_messageTextSize, btnTextSize
            )
            if (a.hasValue(R.styleable.AlertDialog_mc_messageTextColor)) {
                messageTextColor = a.getColorStateList(
                    R.styleable.AlertDialog_mc_messageTextColor
                )!!
            }

            //list
            listItemRes = a.getResourceId(
                R.styleable.AlertDialog_mc_listItemRes, R.layout.mc_dialog_alert_list_item
            )
            if (a.hasValue(R.styleable.AlertDialog_mc_listTextColor)) {
                listTextColor = a.getColorStateList(R.styleable.AlertDialog_mc_listTextColor)!!
            }
            listTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_listTextSize, dp2px(16f)
            )
            listDividerColor = a.getColor(
                R.styleable.AlertDialog_mc_listDividerColor, context.getColor(R.color.dividerColor)
            )
            listDividerHeight = a.getDimension(
                R.styleable.AlertDialog_mc_listDividerHeight, dp2px(0.5f)
            )
            listDividerVisible = a.getBoolean(R.styleable.AlertDialog_mc_listDividerVisible, false)

            //window
            windowWidth = a.getDimensionPixelOffset(R.styleable.AlertDialog_mc_windowWidth, -2)
            windowHeight = a.getDimensionPixelOffset(R.styleable.AlertDialog_mc_windowHeight, -2)
            windowColor = a.getColor(R.styleable.AlertDialog_mc_windowColor, Color.WHITE)
            windowAmount = a.getFloat(R.styleable.AlertDialog_mc_windowAmount, 0.5f)
            windowRadius = a.getDimension(
                R.styleable.AlertDialog_mc_windowRadius, dp2px(6f)
            )
            windowGravity = getGravity(a.getInt(R.styleable.AlertDialog_mc_windowGravity, 4))
            windowDrawable = a.getDrawable(R.styleable.AlertDialog_mc_windowDrawable)
            a.recycle()
        }

        private fun getGravity(gravity: Int): Int {
            return when (gravity) {
                0 -> Gravity.START
                1 -> Gravity.TOP
                2 -> Gravity.END
                3 -> Gravity.BOTTOM
                4 -> Gravity.CENTER
                else -> Gravity.START
            }
        }

        fun setTitle(title: CharSequence): Builder {
            titleText = title
            return this
        }

        fun setTitleTextSize(textSize: Float): Builder {
            titleTextSize = textSize
            return this
        }

        fun setTitleTextColor(@ColorInt textColor: Int): Builder {
            titleTextColor = ColorStateList.valueOf(textColor)
            return this
        }

        fun setTitleTextColor(textColor: ColorStateList): Builder {
            titleTextColor = textColor
            return this
        }

        fun setMessage(message: CharSequence): Builder {
            messageText = message
            return this
        }

        fun setMessageTextSize(textSize: Float): Builder {
            messageTextSize = textSize
            return this
        }

        fun setMessageTextColor(@ColorInt textColor: Int): Builder {
            messageTextColor = ColorStateList.valueOf(textColor)
            return this
        }

        fun setMessageTextColor(textColor: ColorStateList): Builder {
            messageTextColor = textColor
            return this
        }

        fun setPositiveText(text: CharSequence): Builder {
            positiveText = text
            return this
        }

        fun setNegativeText(text: CharSequence): Builder {
            negativeText = text
            return this
        }

        fun setNeutralText(text: CharSequence): Builder {
            neutralText = text
            return this
        }

        fun setOnPositiveClickListener(listener: OnClickListener?): Builder {
            positiveClickListener = listener
            return this
        }

        fun setOnNegativeClickListener(listener: OnClickListener?): Builder {
            negativeClickListener = listener
            return this
        }

        fun setOnNeutralClickListener(listener: OnClickListener?): Builder {
            neutralClickListener = listener
            return this
        }

        fun setOnItemSelectedListener(listener: OnItemSelectedListener?): Builder {
            onItemSelectedListener = listener
            return this
        }

        fun setPositiveButton(text: CharSequence, listener: OnClickListener? = null): Builder {
            positiveText = text
            positiveClickListener = listener
            return this
        }

        fun setNegativeButton(text: CharSequence, listener: OnClickListener? = null): Builder {
            negativeText = text
            negativeClickListener = listener
            return this
        }

        fun setNeutralButton(text: CharSequence, listener: OnClickListener? = null): Builder {
            neutralText = text
            neutralClickListener = listener
            return this
        }

        fun setPositiveTextColor(color: ColorStateList): Builder {
            positiveTextColor = color
            return this
        }

        fun setNegativeTextColor(color: ColorStateList): Builder {
            negativeTextColor = color
            return this
        }

        fun setNeutralTextColor(color: ColorStateList): Builder {
            neutralTextColor = color
            return this
        }

        fun setPositiveTextColor(drawable: Int): Builder {
            positiveTextColor = context.getColorStateList(drawable)
            return this
        }

        fun setNegativeTextColor(drawable: Int): Builder {
            negativeTextColor = context.getColorStateList(drawable)
            return this
        }

        fun setNeutralTextColor(drawable: Int): Builder {
            neutralTextColor = context.getColorStateList(drawable)
            return this
        }

        fun setPositiveTextSize(textSize: Float): Builder {
            positiveTextSize = textSize
            return this
        }

        fun setNegativeTextSize(textSize: Float): Builder {
            negativeTextSize = textSize
            return this
        }

        fun setNeutralTextSize(textSize: Float): Builder {
            neutralTextSize = textSize
            return this
        }

        fun setPositiveBackground(background: Drawable): Builder {
            positiveBackground = background
            return this
        }

        fun setNegativeBackground(background: Drawable): Builder {
            negativeBackground = background
            return this
        }

        fun setNeutralBackground(background: Drawable): Builder {
            neutralBackground = background
            return this
        }

        fun setPositiveBackground(@DrawableRes background: Int): Builder {
            positiveBackground = AppCompatResources.getDrawable(context, background)
            return this
        }

        fun setNegativeBackground(@DrawableRes background: Int): Builder {
            negativeBackground = AppCompatResources.getDrawable(context, background)
            return this
        }

        fun setNeutralBackground(@DrawableRes background: Int): Builder {
            neutralBackground = AppCompatResources.getDrawable(context, background)
            return this
        }

        fun setOnDismissListener(listener: DialogInterface.OnDismissListener?): Builder {
            onDismissListener = listener
            return this
        }

        fun setOnCancelListener(listener: DialogInterface.OnCancelListener?): Builder {
            onCancelListener = listener
            return this
        }

        fun setItems(@ArrayRes itemsId: Int, listener: OnItemClickListener? = null): Builder {
            return setItems(context.resources.getTextArray(itemsId), listener)
        }

        fun setItems(array: Array<out Any>?, listener: OnItemClickListener? = null): Builder {
            return setItems(array?.toList(), listener)
        }

        fun setItems(list: List<Any>?, listener: OnItemClickListener? = null): Builder {
            listAdapter = Adapter(list)
            listAdapter!!.showType = Adapter.TYPE_ITEM
            listAdapter!!.onItemClickListener = listener
            return this
        }

        /**
         * 设置单项选择的列表
         * @param itemsId 资源Id
         * @param checkedItem 默认选中的position，小于0表示不选中
         * @param listener OnItemClickListener?
         * @return Builder
         */
        fun setSingleChoiceItems(
            @ArrayRes itemsId: Int, checkedItem: Int, listener: OnItemClickListener? = null
        ): Builder {
            return setSingleChoiceItems(
                context.resources.getTextArray(itemsId), checkedItem, listener
            )
        }

        fun setSingleChoiceItems(
            array: Array<out Any>?, checkedItem: Int, listener: OnItemClickListener? = null
        ): Builder {
            return setSingleChoiceItems(array?.toList(), checkedItem, listener)
        }

        fun setSingleChoiceItems(
            list: List<Any>?, checkedItem: Int, listener: OnItemClickListener? = null
        ): Builder {
            listAdapter = Adapter(list)
            if (checkedItem >= 0) {
                listAdapter!!.checkedItems.add(checkedItem)
            }
            listAdapter!!.showType = Adapter.TYPE_SINGLE_CHOICE
            listAdapter!!.onItemClickListener = listener
            return this
        }

        /**
         * 设置多项选择的列表
         * @param itemsId 资源id
         * @param checkedItems 默认选择的position，如果为空，表示不选择
         * @param listener OnMultiItemClickListener?
         * @return Builder
         */
        fun setMultiChoiceItems(
            @ArrayRes itemsId: Int, checkedItems: IntArray,
            listener: OnMultiItemClickListener? = null
        ): Builder {
            return setMultiChoiceItems(
                context.resources.getTextArray(itemsId), checkedItems, listener
            )
        }

        fun setMultiChoiceItems(
            array: Array<out Any>?, checkedItems: IntArray,
            listener: OnMultiItemClickListener? = null
        ): Builder {
            return setMultiChoiceItems(array?.toList(), checkedItems.toList(), listener)
        }

        fun setMultiChoiceItems(
            list: List<Any>?, checkedItems: List<Int>, listener: OnMultiItemClickListener? = null
        ): Builder {
            listAdapter = Adapter(list)
            listAdapter!!.checkedItems.addAll(checkedItems)
            listAdapter!!.showType = Adapter.TYPE_MULTI_CHOICE
            listAdapter!!.onMultiItemClickListener = listener
            return this
        }

        fun setListTextColor(@ColorInt textColor: Int): Builder {
            listTextColor = ColorStateList.valueOf(textColor)
            return this
        }

        fun setListTextSize(textSize: Float): Builder {
            listTextSize = textSize
            return this
        }

        /**
         * 设置List的Item时，自定义的Item必须包含有对应的id，[android.widget.CheckBox]id为checkBox
         * [android.widget.RadioButton]id为radioButton，[TextView]id为text
         * @param layoutRes Int
         * @return Builder
         */
        fun setListItemLayout(@LayoutRes layoutRes: Int): Builder {
            listItemRes = layoutRes
            return this
        }

        fun setListDividerHeight(height: Float): Builder {
            listDividerHeight = height
            return this
        }

        fun setListDividerColor(@ColorInt color: Int): Builder {
            listDividerColor = color
            return this
        }

        fun setListDividerVisible(visible: Boolean): Builder {
            listDividerVisible = visible
            return this
        }

        /**
         * 设置[cancelable]为false时，如果[canceledOnTouchOutside]为true会导致其失效，所以当[cancelable]
         * 为false时，手动设置[canceledOnTouchOutside]为false
         * @param cancelable Boolean
         * @return Builder
         */
        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            if (!this.cancelable) {
                canceledOnTouchOutside = false
            }
            return this
        }

        fun setCanceledOnTouchOutside(canceled: Boolean): Builder {
            canceledOnTouchOutside = canceled
            return this
        }

        fun setView(view: View): Builder {
            setView(
                view, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            return this
        }

        fun setView(view: View, layoutParams: FrameLayout.LayoutParams): Builder {
            customView = view
            viewLayoutParams = layoutParams
            return this
        }

        fun setView(@LayoutRes layoutRes: Int): Builder {
            viewLayoutRes = layoutRes
            viewLayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            return this
        }

        fun setWindowGravity(gravity: Int): Builder {
            windowGravity = gravity
            return this
        }

        /**
         * 设置dialog在X方向上的偏移量，根据[windowGravity]包含[Gravity.START]/[Gravity.END]
         * 来决定具体是哪个方向上的偏移
         * @param offset 负值无效
         * @return Builder
         */
        fun setXOffset(offset: Int): Builder {
            xOffset = offset
            return this
        }

        /**
         * 设置dialog在Y方向上的偏移量，根据[windowGravity]包含[Gravity.TOP]/[Gravity.BOTTOM]
         * 来决定具体是哪个方向上的偏移
         * @param offset 负值无效
         * @return Builder
         */
        fun setYOffset(offset: Int): Builder {
            yOffset = offset
            return this
        }

        fun setWindowAmount(amount: Float): Builder {
            windowAmount = amount
            return this
        }

        fun setWindowWidth(width: Int): Builder {
            windowWidth = width
            return this
        }

        fun setWindowHeight(height: Int): Builder {
            windowHeight = height
            return this
        }

        fun setWindowColor(@ColorInt color: Int): Builder {
            windowColor = color
            return this
        }

        fun setWindowRadius(radius: Float): Builder {
            windowRadius = radius
            return this
        }

        fun setWindowDrawable(drawable: Drawable): Builder {
            windowDrawable = drawable
            return this
        }

        fun withAnimation(aniStyle: Int): Builder {
            windowAnimation = aniStyle
            return this
        }

        fun create(): AlertDialog {
            val alertDialog = AlertDialog(context)
            setDialogInternal(alertDialog)
            alertDialog.dialogFragment.isCancelable = cancelable
            alertDialog.dialogFragment.setCanceledOnTouchOutside(canceledOnTouchOutside)
                .setOnDismissListener(onDismissListener)
                .setOnCancelListener(onCancelListener)
                .setXOffset(xOffset)
                .setYOffset(yOffset)
                .setWindowAnimate(windowAnimation)
                .setWindowAmount(windowAmount)
                .setWindowGravity(windowGravity)
                .setWindowColor(windowColor)
                .setWindowRadius(windowRadius)
                .setWindowDrawable(windowDrawable)
                .setWindowWidth(windowWidth)
                .setWindowHeight(windowHeight)
            return alertDialog
        }

        fun show(tag: String = "default"): AlertDialog {
            val dialog = create()
            dialog.show(tag)
            return dialog
        }

        private fun setDialogInternal(alertDialog: AlertDialog) {
            with(alertDialog.titleView) {
                setTextColor(titleTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
                text = titleText
                gravity = titleGravity
                isVisible = !titleText.isNullOrEmpty()
            }
            with(alertDialog.messageView) {
                setTextColor(messageTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, messageTextSize)
                text = messageText
            }
            with(alertDialog.positiveButton) {
                text = positiveText ?: ""
                setTextColor(positiveTextColor)
                background = positiveBackground
                isVisible = !positiveText.isNullOrEmpty()
                setOnClickListener {
                    positiveClickListener?.onClick(alertDialog) ?: alertDialog.dismiss()
                }
            }
            with(alertDialog.negativeButton) {
                text = negativeText ?: ""
                setTextColor(negativeTextColor)
                background = negativeBackground
                isVisible = !negativeText.isNullOrEmpty()
                setOnClickListener {
                    negativeClickListener?.onClick(alertDialog) ?: alertDialog.dismiss()
                }
            }
            with(alertDialog.neutralButton) {
                text = neutralText ?: ""
                setTextColor(neutralTextColor)
                background = neutralBackground
                isVisible = !neutralText.isNullOrEmpty()
                setOnClickListener {
                    neutralClickListener?.onClick(alertDialog) ?: alertDialog.dismiss()
                }
            }
            with(alertDialog.binding) {
                //没有标题，隐藏标题的头部Space
                titleSpace.isVisible = !tvTitle.isVisible
                //没有内容，隐藏ScrollView
                scrollView.isVisible = !messageText.isNullOrEmpty()
                //没有自定义View，隐藏自定义View的容器
                val hasCustomView = customView != null && viewLayoutRes != 0
                viewContainer.isVisible =
                    !scrollView.isVisible && (hasCustomView || listAdapter != null)
                //没有按钮，隐藏按钮的Layout，如果有按钮，为ScrollView设置一个最小高度
                val hasBtn = btnPositive.isVisible || btnNegative.isVisible || btnNeutral.isVisible
                containerBtn.isVisible = hasBtn
                scrollView.minimumHeight = dp2px(46f).toInt()

                //如果有ScrollView，设置indicators
                if (scrollView.isVisible) {
                    setScrollIndicators(scrollView, this)
                }

                //如果自定义View的容器没有显示，不再继续执行
                if (!viewContainer.isVisible) {
                    return
                }
                when {
                    customView != null -> {
                        alertDialog.viewContainer.addView(customView, viewLayoutParams)
                    }
                    viewLayoutRes != 0 -> {
                        customView = View.inflate(context, viewLayoutRes, null)
                        alertDialog.viewContainer.addView(customView, viewLayoutParams)
                    }
                    listAdapter != null -> {
                        val recyclerView = RecyclerView(context)
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        if (listDividerVisible) {
                            recyclerView.addItemDecoration(
                                RecyclerViewDivider(
                                    RecyclerViewDivider.HORIZONTAL, listDividerHeight.toInt(),
                                    listDividerColor
                                )
                            )
                        }
                        listAdapter!!.dialog = alertDialog
                        listAdapter!!.layoutRes = listItemRes
                        listAdapter!!.onItemSelectedListener = onItemSelectedListener
                        recyclerView.adapter = listAdapter
                        recyclerView.clipToPadding = false
                        alertDialog.viewContainer.addView(recyclerView)
                        //去掉父容器的padding，设置到RecyclerView上
                        recyclerView.setPaddingTop(viewContainer.paddingTop)
                        recyclerView.setPaddingBottom(viewContainer.paddingBottom)
                        viewContainer.setPadding(0)
                        setScrollIndicators(recyclerView, this)
                    }
                }
            }
        }

        private fun setScrollIndicators(contentView: View, binding: McDialogAlertBinding) {
            if (contentView.isVisible) {
                binding.btnDivider.isVisible = true
                val btnVisible = binding.containerBtn.isVisible
                contentView.post {
                    binding.btnDivider.isVisible = contentView.canScrollVertically(1) && btnVisible
                }
                contentView.setOnScrollChangeListener { v, _, _, _, _ ->
                    val canScrollDown = v.canScrollVertically(-1)
                    val canScrollUp = v.canScrollVertically(1)
                    binding.titleDivider.isVisible = canScrollDown && !titleText.isNullOrEmpty()
                    binding.btnDivider.isVisible = canScrollUp && btnVisible
                }
            }
        }
    }

    private class Adapter(private val list: List<Any>?) : RecyclerView.Adapter<ViewHolder>() {

        lateinit var dialog: AlertDialog
        var layoutRes = 0
        var showType = 0
        var checkedItems = mutableListOf<Int>()
        var onItemClickListener: OnItemClickListener? = null
        var onMultiItemClickListener: OnMultiItemClickListener? = null
        var onItemSelectedListener: OnItemSelectedListener? = null

        companion object {
            const val TYPE_ITEM = 0
            const val TYPE_SINGLE_CHOICE = 1
            const val TYPE_MULTI_CHOICE = 2
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflater(layoutRes))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val checkBox = holder.itemView.findViewById<CheckBox>(R.id.checkbox)
            val radioButton = holder.itemView.findViewById<RadioButton>(R.id.radioButton)
            val textView = holder.itemView.findViewById<TextView>(R.id.text)
            when (showType) {
                TYPE_ITEM -> {
                    checkBox.visibility = View.GONE
                    radioButton.visibility = View.GONE
                }
                TYPE_SINGLE_CHOICE -> {
                    checkBox.visibility = View.GONE
                    radioButton.visibility = View.VISIBLE
                    radioButton.isChecked = checkedItems.contains(position)
                }
                TYPE_MULTI_CHOICE -> {
                    radioButton.visibility = View.GONE
                    checkBox.visibility = View.VISIBLE
                    checkBox.isChecked = checkedItems.contains(position)
                }
            }
            val any = list!![position]
            textView.text = when (any) {
                is IListItemData -> any.getItemText()
                else -> any.toString()
            }
            holder.itemView.setOnClickListener {
                when (showType) {
                    TYPE_ITEM -> {
                        onItemClickListener?.onItemClick(dialog, it, position) ?: dialog.dismiss()
                    }
                    TYPE_SINGLE_CHOICE -> {
                        if (checkedItems.isNotEmpty()) {
                            notifyItemChanged(checkedItems.removeAt(0))
                        }
                        radioButton.isChecked = !radioButton.isChecked
                        if (radioButton.isChecked) {
                            checkedItems.add(position)
                        } else {
                            checkedItems.remove(position)
                        }
                        notifyItemChanged(position)
                        onItemSelectedListener?.onItemSelected(dialog, radioButton, checkedItems)
                        onItemClickListener?.onItemClick(dialog, it, position) ?: dialog.dismiss()
                    }
                    TYPE_MULTI_CHOICE -> {
                        checkBox.isChecked = !checkBox.isChecked
                        if (checkBox.isChecked) {
                            checkedItems.add(position)
                        } else {
                            checkedItems.remove(position)
                        }
                        notifyItemChanged(position)
                        onItemSelectedListener?.onItemSelected(dialog, checkBox, checkedItems)
                        onMultiItemClickListener?.onItemClick(
                            dialog, it, checkBox.isChecked, position
                        )
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }
    }
}