package com.masterchan.lib.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.masterchan.lib.R
import com.masterchan.lib.ext.inflater
import com.masterchan.lib.utils.BitmapUtils
import com.masterchan.lib.widget.RecyclerViewDivider
import com.masterchan.lib.widget.ViewHolder

/**
 * @author MasterChan
 * @date 2021-12-29 09:53
 * @describe GridImageView
 */
@Suppress("MemberVisibilityCanBePrivate")
@SuppressLint("NotifyDataSetChanged")
class GridImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * 一行多少列
     */
    var spanCount = 3
        private set

    /**
     * 最大显示个数
     */
    var itemMax = 9
        private set

    /**
     * 是否显示删除按钮
     */
    var showDeleteBtn = true
        private set

    private val mRecyclerView = RecyclerView(context)
    private val mLayoutManager = GridLayoutManager(context, spanCount)
    private val mDataList = mutableListOf<Any>()
    private val mAdapter = Adapter()
    private var mItemDivider: RecyclerView.ItemDecoration? = null
    private var mAddImageRes = R.drawable.ic_add_24_4b4b4b
    private var mDeleteImageRes = R.drawable.ic_clear_24_red
    private var mAddImageScaleType = ImageView.ScaleType.CENTER
    private var mDeleteImageScaleType = ImageView.ScaleType.CENTER
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnAddBtnClickListener: OnAddBtnClickListener? = null
    private var mOnImageDeleteListener: OnImageDeleteListener? = null

    companion object {
        var imageFactory: ImageLoadFactory = DefaultImageLoadFactory()
    }

    init {
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter
        mRecyclerView.hasFixedSize()
        mRecyclerView.isNestedScrollingEnabled = false
        addView(mRecyclerView)
    }

    fun setSpanCount(spanCount: Int): GridImageView {
        this.spanCount = spanCount
        mLayoutManager.spanCount = spanCount
        return this
    }

    fun setSpace(width: Int, @ColorInt color: Int): GridImageView {
        mItemDivider?.let { mRecyclerView.removeItemDecoration(it) }
        mItemDivider = RecyclerViewDivider(RecyclerViewDivider.BOTH, width, color)
        mRecyclerView.addItemDecoration(mItemDivider!!)
        return this
    }

    fun setItemMax(max: Int): GridImageView {
        itemMax = max
        mAdapter.notifyDataSetChanged()
        return this
    }

    fun setAddBtnImage(@DrawableRes image: Int): GridImageView {
        mAddImageRes = image
        return this
    }

    fun setDeleteBtnImage(@DrawableRes image: Int): GridImageView {
        mDeleteImageRes = image
        return this
    }

    fun setAddBtnScaleType(scaleType: ImageView.ScaleType): GridImageView {
        mAddImageScaleType = scaleType
        return this
    }

    fun setDeleteBtnScaleType(scaleType: ImageView.ScaleType): GridImageView {
        mDeleteImageScaleType = scaleType
        return this
    }

    fun showDeleteBtn(show: Boolean): GridImageView {
        showDeleteBtn = show
        return this
    }

    fun setImageFactory(factory: ImageLoadFactory): GridImageView {
        imageFactory = factory
        return this
    }

    fun setOnItemClickListener(listener: OnItemClickListener): GridImageView {
        mOnItemClickListener = listener
        return this
    }

    fun setOnAddBtnClickListener(listener: OnAddBtnClickListener): GridImageView {
        mOnAddBtnClickListener = listener
        return this
    }

    fun setOnImageDeleteClickListener(listener: OnImageDeleteListener): GridImageView {
        mOnImageDeleteListener = listener
        return this
    }

    fun addImage(any: Any): GridImageView {
        mDataList.add(any)
        mAdapter.notifyItemInserted(mDataList.lastIndex)
        return this
    }

    fun addImages(images: List<Any>): GridImageView {
        if (images.isNotEmpty()) {
            mDataList.addAll(images)
            mAdapter.notifyDataSetChanged()
        }
        return this
    }

    fun getImages(): List<Any> {
        return mDataList
    }

    fun removeImage(position: Int) {
        mDataList.removeAt(position)
        mAdapter.notifyItemRemoved(position)
    }

    fun removeImage(any: Any) {
        removeImage(mDataList.indexOf(any))
    }

    fun removeAll() {
        mDataList.clear()
        mAdapter.notifyDataSetChanged()
    }

    fun interface ImageLoadFactory {
        fun loadImage(context: Context, any: Any, imageView: ImageView)
    }

    fun interface OnImageDeleteListener {
        fun onImageDelete(position: Int, image: Any)
    }

    fun interface OnItemClickListener {
        fun onItemClick(v: View, position: Int, any: Any)
    }

    fun interface OnAddBtnClickListener {
        fun onAddBtnClick(v: View)
    }

    class DefaultImageLoadFactory : ImageLoadFactory {
        override fun loadImage(context: Context, any: Any, imageView: ImageView) {
            imageView.setImageBitmap(
                when (any) {
                    is Int -> AppCompatResources.getDrawable(context, any)?.toBitmap()
                    is String -> BitmapUtils.getBitmap(any)
                    is Drawable -> any.toBitmap()
                    is Bitmap -> any
                    else -> null
                }
            )
        }
    }

    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent.inflater(R.layout.mc_grid_image_view_item))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val imageView = holder.getImageView(R.id.iv_image)!!
            val deleteView = holder.getImageView(R.id.iv_delete)!!
            val viewType = getItemViewType(position)
            if (viewType == -1) {
                deleteView.isVisible = false
                deleteView.setOnClickListener(null)
                imageView.scaleType = mAddImageScaleType
                imageView.setImageResource(mAddImageRes)
                deleteView.setImageDrawable(null)
                mOnAddBtnClickListener?.let {
                    holder.itemView.setOnClickListener { v -> it.onAddBtnClick(v) }
                }
            } else {
                deleteView.scaleType = mDeleteImageScaleType
                deleteView.isVisible = showDeleteBtn
                deleteView.setOnClickListener {
                    val image = mDataList.removeAt(position)
                    notifyDataSetChanged()
                    mOnImageDeleteListener?.onImageDelete(position, image)
                }
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                imageFactory.loadImage(context, mDataList[position], imageView)
                imageFactory.loadImage(context, mDeleteImageRes, deleteView)
                mOnItemClickListener?.let {
                    holder.itemView.setOnClickListener { v: View ->
                        it.onItemClick(v, position, mDataList[position])
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                mDataList.isEmpty() -> -1
                mDataList.size >= itemMax -> super.getItemViewType(position)
                position == itemCount - 1 -> -1
                else -> super.getItemViewType(position)
            }
        }

        override fun getItemCount(): Int {
            return when {
                mDataList.isEmpty() -> 1
                mDataList.size >= itemMax -> itemMax
                else -> mDataList.size + 1
            }
        }
    }
}