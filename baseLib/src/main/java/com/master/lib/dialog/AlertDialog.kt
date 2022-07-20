package com.master.lib.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.master.lib.dialog.AlertDialog.Companion.TYPE_ITEM
import com.master.lib.dialog.AlertDialog.Companion.TYPE_MULTI_CHOICE
import com.master.lib.dialog.AlertDialog.Companion.TYPE_SINGLE_CHOICE
import com.master.lib.ext.*
import com.master.lib.widget.RecyclerViewDivider
import com.master.lib.widget.ViewHolder
import com.masterchan.lib.R

/**
 * AlertDialog
 * @author MasterChan
 * @date 2021-12-14 10:51
 */
@Suppress("MemberVisibilityCanBePrivate")
open class AlertDialog private constructor(context: Context) :
    BaseDialog(context, R.layout.mc_dialog_alert) {

    val titleView: TextView = contentView!!.findViewById(R.id.tv_title)
    val messageView: TextView = contentView!!.findViewById(R.id.tv_message)
    val positiveButton: Button = contentView!!.findViewById(R.id.btn_positive)
    val negativeButton: Button = contentView!!.findViewById(R.id.btn_negative)
    val neutralButton: Button = contentView!!.findViewById(R.id.btn_neutral)
    val viewContainer: FrameLayout = contentView!!.findViewById(R.id.view_container)
    private val titleSpace: Space = contentView!!.findViewById(R.id.titleSpace)
    private val scrollView: ScrollView = contentView!!.findViewById(R.id.scrollView)
    private val btnContainer: LinearLayout = contentView!!.findViewById(R.id.container_btn)
    private val btnDivider: View = contentView!!.findViewById(R.id.btnDivider)
    private val titleDivider: View = contentView!!.findViewById(R.id.titleDivider)

    /**
     * dialog的button点击事件，包括[positiveButton]、[negativeButton]、[neutralButton]
     */
    fun interface OnClickListener {
        fun onClick(dialog: AlertDialog)
    }

    /**
     * [Adapter.showType]为[TYPE_ITEM]时的item点击事件，会和[OnItemSelectedListener]一起触发
     */
    fun interface OnItemClickListener {
        fun onItemClick(dialog: AlertDialog, view: View, position: Int)
    }

    /**
     * [Adapter.showType]为[TYPE_MULTI_CHOICE]时的item点击事件，会和[OnItemSelectedListener]一起触发
     */
    fun interface OnMultiItemClickListener {
        fun onItemClick(dialog: AlertDialog, view: View, checked: Boolean, position: Int)
    }

    /**
     * [Adapter.showType]为[TYPE_SINGLE_CHOICE]或者[TYPE_MULTI_CHOICE]时的item点击事件，
     * 和[OnItemClickListener]、[OnMultiItemClickListener]一起触发
     */
    fun interface OnItemSelectedListener {
        fun onItemSelected(dialog: AlertDialog, view: CompoundButton, checkedItems: List<Int>)
    }

    /**
     * [Builder.setItems]方法设置的Entity可以扩展该接口，返回需要在列表中显示的数据
     */
    fun interface IListItemData {
        fun getItemText(): String
    }

    class Builder(private val context: Context, styleRes: Int = R.style.mc_AlertDialog) {

        private var positiveTextSize: Float
        private var negativeTextSize: Float
        private var neutralTextSize: Float

        private val btnTextColor = context.getColorStateList(R.color.color_alert_dialog_button)
        private val contentTextColor = context.getColorStateList(R.color.color_alert_dialog_text)
        private var positiveTextColor = btnTextColor
        private var negativeTextColor = btnTextColor
        private var neutralTextColor = btnTextColor
        private var titleTextColor = contentTextColor
        private var messageTextColor = contentTextColor

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
        private var listTextColor = context.getColorStateList(R.color.color_alert_dialog_text)
        private var listTextSize: Float
        private var listDividerVisible: Boolean
        private var listDividerHeight: Float
        private var listDividerColor: Int
        private var listAdapter: Adapter? = null

        private var positiveClickListener: OnClickListener? = null
        private var negativeClickListener: OnClickListener? = null
        private var neutralClickListener: OnClickListener? = null
        private var onItemSelectedListener: OnItemSelectedListener? = null

        private var customView: View? = null
        private var viewLayoutRes = 0
        private var viewLayoutParams: FrameLayout.LayoutParams? = null

        init {
            val a = context.theme.obtainStyledAttributes(
                null, R.styleable.AlertDialog, R.attr.mc_AlertDialogDefaultStyle, styleRes
            )

            //button textSize
            val btnTextSize = a.getDimension(R.styleable.AlertDialog_mc_btnTextSize, dp2px(16f))
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
                val textColor = a.getColorStateList(R.styleable.AlertDialog_mc_btnTextColor)!!
                positiveTextColor = textColor
                negativeTextColor = textColor
                neutralTextColor = textColor
            }
            a.ifHas(R.styleable.AlertDialog_mc_positiveTextColor) {
                positiveTextColor = a.getColorStateList(it)!!
            }
            a.ifHas(R.styleable.AlertDialog_mc_negativeTextColor) {
                negativeTextColor = a.getColorStateList(it)!!
            }
            a.ifHas(R.styleable.AlertDialog_mc_neutralTextColor) {
                neutralTextColor = a.getColorStateList(it)!!
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
            titleGravity = a.getInt(R.styleable.AlertDialog_mc_titleGravity, Gravity.START)
            titleTextSize = a.getDimension(R.styleable.AlertDialog_mc_titleTextSize, dp2px(18f))
            if (a.hasValue(R.styleable.AlertDialog_mc_titleTextColor)) {
                titleTextColor = a.getColorStateList(R.styleable.AlertDialog_mc_titleTextColor)!!
            }

            //message
            messageTextSize = a.getDimension(
                R.styleable.AlertDialog_mc_messageTextSize, btnTextSize
            )
            a.ifHas(R.styleable.AlertDialog_mc_messageTextColor) {
                messageTextColor = a.getColorStateList(it)!!
            }

            //list
            listItemRes = a.getResourceId(
                R.styleable.AlertDialog_mc_listItemRes, R.layout.mc_dialog_alert_list_item
            )
            if (a.hasValue(R.styleable.AlertDialog_mc_listTextColor)) {
                listTextColor = a.getColorStateList(R.styleable.AlertDialog_mc_listTextColor)!!
            }
            listTextSize = a.getDimension(R.styleable.AlertDialog_mc_listTextSize, dp2px(16f))
            listDividerColor = a.getColor(
                R.styleable.AlertDialog_mc_listDividerColor,
                context.getColor(R.color.color_alert_dialog_divider)
            )
            listDividerHeight = a.getDimension(
                R.styleable.AlertDialog_mc_listDividerHeight, dp2px(0.5f)
            )
            listDividerVisible = a.getBoolean(R.styleable.AlertDialog_mc_listDividerVisible, false)

            a.recycle()
        }

        fun setTitle(title: CharSequence) = apply {
            titleText = title
        }

        fun setTitle(@StringRes idRes: Int) = apply {
            titleText = context.getText(idRes)
        }

        fun setTitleTextSize(textSize: Float) = apply {
            titleTextSize = textSize
        }

        fun setTitleTextColor(@ColorInt textColor: Int) = apply {
            titleTextColor = ColorStateList.valueOf(textColor)
        }

        fun setTitleTextColor(textColor: ColorStateList) = apply {
            titleTextColor = textColor
        }

        fun setMessage(message: CharSequence) = apply {
            messageText = message
        }

        fun setMessage(@StringRes idRes: Int) = apply {
            messageText = context.getText(idRes)
        }

        fun setMessageTextSize(textSize: Float) = apply {
            messageTextSize = textSize
        }

        fun setMessageTextColor(@ColorInt textColor: Int) = apply {
            messageTextColor = ColorStateList.valueOf(textColor)
        }

        fun setMessageTextColor(textColor: ColorStateList) = apply {
            messageTextColor = textColor
        }

        fun setPositiveText(text: CharSequence) = apply {
            positiveText = text
        }

        fun setPositiveText(@StringRes idRes: Int) = apply {
            positiveText = context.getText(idRes)
        }

        fun setNegativeText(text: CharSequence) = apply {
            negativeText = text
        }

        fun setNegativeText(@StringRes idRes: Int) = apply {
            negativeText = context.getText(idRes)
        }

        fun setNeutralText(text: CharSequence) = apply {
            neutralText = text
        }

        fun setNeutralText(@StringRes idRes: Int) = apply {
            neutralText = context.getText(idRes)
        }

        fun setOnPositiveClickListener(listener: OnClickListener?) = apply {
            positiveClickListener = listener
        }

        fun setOnNegativeClickListener(listener: OnClickListener?) = apply {
            negativeClickListener = listener
        }

        fun setOnNeutralClickListener(listener: OnClickListener?) = apply {
            neutralClickListener = listener
        }

        fun setOnItemSelectedListener(listener: OnItemSelectedListener?) = apply {
            onItemSelectedListener = listener
        }

        fun setPositiveButton(text: CharSequence, listener: OnClickListener? = null) = apply {
            positiveText = text
            positiveClickListener = listener
        }

        fun setNegativeButton(text: CharSequence, listener: OnClickListener? = null) = apply {
            negativeText = text
            negativeClickListener = listener
        }

        fun setNeutralButton(text: CharSequence, listener: OnClickListener? = null) = apply {
            neutralText = text
            neutralClickListener = listener
        }

        fun setPositiveTextColor(color: ColorStateList) = apply {
            positiveTextColor = color
        }

        fun setNegativeTextColor(color: ColorStateList) = apply {
            negativeTextColor = color
        }

        fun setNeutralTextColor(color: ColorStateList) = apply {
            neutralTextColor = color
        }

        fun setPositiveTextColor(drawable: Int) = apply {
            positiveTextColor = context.getColorStateList(drawable)
        }

        fun setNegativeTextColor(drawable: Int) = apply {
            negativeTextColor = context.getColorStateList(drawable)
        }

        fun setNeutralTextColor(drawable: Int) = apply {
            neutralTextColor = context.getColorStateList(drawable)
        }

        fun setPositiveTextSize(textSize: Float) = apply {
            positiveTextSize = textSize
        }

        fun setNegativeTextSize(textSize: Float) = apply {
            negativeTextSize = textSize
        }

        fun setNeutralTextSize(textSize: Float) = apply {
            neutralTextSize = textSize
        }

        fun setPositiveBackground(background: Drawable) = apply {
            positiveBackground = background
        }

        fun setNegativeBackground(background: Drawable) = apply {
            negativeBackground = background
        }

        fun setNeutralBackground(background: Drawable) = apply {
            neutralBackground = background
        }

        fun setPositiveBackground(@DrawableRes background: Int) = apply {
            positiveBackground = AppCompatResources.getDrawable(context, background)
        }

        fun setNegativeBackground(@DrawableRes background: Int) = apply {
            negativeBackground = AppCompatResources.getDrawable(context, background)
        }

        fun setNeutralBackground(@DrawableRes background: Int) = apply {
            neutralBackground = AppCompatResources.getDrawable(context, background)
        }

        fun setItems(@ArrayRes itemsId: Int, listener: OnItemClickListener? = null) = apply {
            return setItems(context.resources.getTextArray(itemsId), listener)
        }

        fun setItems(array: Array<out Any>?, listener: OnItemClickListener? = null) = apply {
            return setItems(array?.toList(), listener)
        }

        fun setItems(list: List<Any>?, listener: OnItemClickListener? = null) = apply {
            listAdapter = Adapter(list)
            listAdapter!!.showType = TYPE_ITEM
            listAdapter!!.onItemClickListener = listener
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
        ) = apply {
            return setSingleChoiceItems(
                context.resources.getTextArray(itemsId), checkedItem, listener
            )
        }

        fun setSingleChoiceItems(
            array: Array<out Any>?, checkedItem: Int, listener: OnItemClickListener? = null
        ) = apply {
            return setSingleChoiceItems(array?.toList(), checkedItem, listener)
        }

        fun setSingleChoiceItems(
            list: List<Any>?, checkedItem: Int, listener: OnItemClickListener? = null
        ) = apply {
            listAdapter = Adapter(list)
            if (checkedItem >= 0) {
                listAdapter!!.checkedItems.add(checkedItem)
            }
            listAdapter!!.showType = TYPE_SINGLE_CHOICE
            listAdapter!!.onItemClickListener = listener
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
        ) = apply {
            return setMultiChoiceItems(
                context.resources.getTextArray(itemsId), checkedItems, listener
            )
        }

        fun setMultiChoiceItems(
            array: Array<out Any>?, checkedItems: IntArray,
            listener: OnMultiItemClickListener? = null
        ) = apply {
            return setMultiChoiceItems(array?.toList(), checkedItems.toList(), listener)
        }

        fun setMultiChoiceItems(
            list: List<Any>?, checkedItems: List<Int>, listener: OnMultiItemClickListener? = null
        ) = apply {
            listAdapter = Adapter(list)
            listAdapter!!.checkedItems.addAll(checkedItems)
            listAdapter!!.showType = TYPE_MULTI_CHOICE
            listAdapter!!.onMultiItemClickListener = listener
        }

        fun setListTextColor(@ColorInt textColor: Int) = apply {
            listTextColor = ColorStateList.valueOf(textColor)
        }

        fun setListTextSize(textSize: Float) = apply {
            listTextSize = textSize
        }

        /**
         * 设置List的Item时，自定义的Item必须包含有对应的id，[android.widget.CheckBox]id为checkBox
         * [android.widget.RadioButton]id为radioButton，[TextView]id为text
         * @param layoutRes Int
         * @return Builder
         */
        fun setListItemLayout(@LayoutRes layoutRes: Int) = apply {
            listItemRes = layoutRes
        }

        fun setListDividerHeight(height: Float) = apply {
            listDividerHeight = height
        }

        fun setListDividerColor(@ColorInt color: Int) = apply {
            listDividerColor = color
        }

        fun setListDividerVisible(visible: Boolean) = apply {
            listDividerVisible = visible
        }

        fun setView(
            view: View,
            layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(-1, -1)
        ) = apply {
            customView = view
            viewLayoutParams = layoutParams
        }

        fun setView(
            @LayoutRes layoutRes: Int,
            layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(-1, -1)
        ) = apply {
            viewLayoutRes = layoutRes
            viewLayoutParams = layoutParams
        }

        fun create(): AlertDialog {
            return AlertDialog(context).apply { setDialogInternal(this) }
        }

        fun show(tag: String = "default"): AlertDialog {
            val dialog = create()
            dialog.show(tag)
            return dialog
        }

        private fun setDialogInternal(alertDialog: AlertDialog) {
            alertDialog.titleView.apply {
                setTextColor(titleTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
                text = titleText
                gravity = titleGravity
                isVisible = !titleText.isNullOrEmpty()
            }
            alertDialog.messageView.apply {
                setTextColor(messageTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, messageTextSize)
                text = messageText
            }
            alertDialog.positiveButton.apply {
                text = positiveText ?: ""
                setTextColor(positiveTextColor)
                background = positiveBackground
                isVisible = !positiveText.isNullOrEmpty()
                setOnClickListener {
                    positiveClickListener?.onClick(alertDialog) ?: alertDialog.dismiss()
                }
            }
            alertDialog.negativeButton.apply {
                text = negativeText ?: ""
                setTextColor(negativeTextColor)
                background = negativeBackground
                isVisible = !negativeText.isNullOrEmpty()
                setOnClickListener {
                    negativeClickListener?.onClick(alertDialog) ?: alertDialog.dismiss()
                }
            }
            alertDialog.neutralButton.apply {
                text = neutralText ?: ""
                setTextColor(neutralTextColor)
                background = neutralBackground
                isVisible = !neutralText.isNullOrEmpty()
                setOnClickListener {
                    neutralClickListener?.onClick(alertDialog) ?: alertDialog.dismiss()
                }
            }
            alertDialog.apply {
                //没有标题，隐藏标题的头部Space
                titleSpace.isVisible = !titleView.isVisible
                //没有内容，隐藏ScrollView
                scrollView.isVisible = !messageText.isNullOrEmpty()
                //没有自定义View，隐藏自定义View的容器
                val hasCustomView = customView != null || viewLayoutRes != 0
                viewContainer.isVisible =
                    !scrollView.isVisible && (hasCustomView || listAdapter != null)
                //没有按钮，隐藏按钮的Layout，如果有按钮，为ScrollView设置一个最小高度
                btnContainer.isVisible =
                    positiveButton.isVisible || negativeButton.isVisible || neutralButton.isVisible
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
                        val recyclerView = RecyclerView(this@Builder.context)
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
                        setScrollIndicators(recyclerView, alertDialog)
                    }
                }
            }
        }

        private fun setScrollIndicators(contentView: View, dialog: AlertDialog) {
            if (contentView.isVisible) {
                dialog.btnDivider.isVisible = true
                val btnVisible = dialog.btnContainer.isVisible
                contentView.post {
                    dialog.btnDivider.isVisible = contentView.canScrollVertically(1) && btnVisible
                }
                contentView.setOnScrollChangeListener { v, _, _, _, _ ->
                    val canScrollDown = v.canScrollVertically(-1)
                    val canScrollUp = v.canScrollVertically(1)
                    dialog.titleDivider.isVisible = canScrollDown && !titleText.isNullOrEmpty()
                    dialog.btnDivider.isVisible = canScrollUp && btnVisible
                }
            }
        }
    }

    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_SINGLE_CHOICE = 1
        const val TYPE_MULTI_CHOICE = 2
    }

    private class Adapter(private val list: List<Any>?) : RecyclerView.Adapter<ViewHolder>() {

        lateinit var dialog: AlertDialog
        var layoutRes = 0
        var showType = 0
        var checkedItems = mutableListOf<Int>()
        var onItemClickListener: OnItemClickListener? = null
        var onMultiItemClickListener: OnMultiItemClickListener? = null
        var onItemSelectedListener: OnItemSelectedListener? = null

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