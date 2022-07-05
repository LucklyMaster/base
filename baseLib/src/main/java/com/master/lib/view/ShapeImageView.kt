package com.master.lib.view

import android.content.Context
import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.masterchan.lib.R
import kotlin.math.min

/**
 * ShapeImageView
 * @author MasterChan
 * @date 2021-12-11 17:44
 */
class ShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val mCircle: Boolean
    private val mStrokeColor: Int
    private val mStrokeWidth: Float
    private val mRadius: Float
    private val mLeftTopRadius: Float
    private val mLeftBottomRadius: Float
    private val mRightTopRadius: Float
    private val mRightBottomRadius: Float

    private val mStokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mStokeRect = RectF()
    private val mBitmapRect = RectF()
    private val mShaderMatrix = Matrix()
    private var mShader: BitmapShader? = null
    private var mBitmap: Bitmap? = null
    private var mRebuildShader = true
    private var mRebuildMatrix = true
    private val mStokePath = Path()
    private val mBitmapPath = Path()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView)
        mCircle = a.getBoolean(R.styleable.ShapeImageView_mc_isCircle, false)
        mStrokeWidth = a.getDimension(R.styleable.ShapeImageView_mc_strokeWidth, 0f)
        mStrokeColor = a.getColor(R.styleable.ShapeImageView_mc_strokeColor, Color.TRANSPARENT)
        mRadius = a.getDimension(R.styleable.ShapeImageView_mc_radius, 0f)
        mLeftTopRadius = a.getDimension(R.styleable.ShapeImageView_mc_leftTopRadius, mRadius)
        mLeftBottomRadius = a.getDimension(R.styleable.ShapeImageView_mc_leftBottomRadius, mRadius)
        mRightTopRadius = a.getDimension(R.styleable.ShapeImageView_mc_rightTopRadius, mRadius)
        mRightBottomRadius = a.getDimension(
            R.styleable.ShapeImageView_mc_rightBottomRadius, mRadius
        )
        a.recycle()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mRebuildShader = true
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mRebuildShader = true
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mRebuildShader = true
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        mRebuildShader = true
    }

    override fun setScaleType(scaleType: ScaleType) {
        super.setScaleType(scaleType)
        mRebuildMatrix = true
    }

    override fun onDraw(canvas: Canvas) {
        mStokePaint.style = Paint.Style.STROKE
        mStokePaint.strokeWidth = mStrokeWidth
        mStokePaint.color = mStrokeColor

        if (drawable == null) {
            super.onDraw(canvas)
            return
        }

        if (mRebuildShader) {
            mBitmap = getBitmap(drawable)
            mShader = BitmapShader(mBitmap!!, TileMode.CLAMP, TileMode.CLAMP)
            mRebuildShader = false
        }

        if (mRebuildMatrix) {
            updateShaderMatrix()
            mShader!!.setLocalMatrix(mShaderMatrix)
            mBitmapPaint.shader = mShader
            mRebuildMatrix = false
        }

        if (mCircle) {
            canvas.drawOval(mBitmapRect, mBitmapPaint)
            if (mStrokeWidth > 0) {
                canvas.drawOval(mStokeRect, mStokePaint)
            }
        } else {
            if (cornerHasRadius()) {
                setRoundPath(mBitmapPath, mBitmapRect)
                canvas.drawPath(mBitmapPath, mBitmapPaint)
                if (mStrokeWidth > 0) {
                    setRoundPath(mStokePath, mStokeRect)
                    canvas.drawPath(mStokePath, mStokePaint)
                }
            } else {
                canvas.drawRect(mBitmapRect, mBitmapPaint)
                if (mStrokeWidth > 0) {
                    canvas.drawRect(mStokeRect, mStokePaint)
                }
            }
        }
    }

    private fun getBitmap(drawable: Drawable?): Bitmap? {
        return when (drawable) {
            is BitmapDrawable -> {
                drawable.bitmap
            }
            is ColorDrawable -> {
                val bounds = drawable.getBounds()
                val color = drawable.color
                val bitmap =
                    Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawARGB(
                    Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color)
                )
                bitmap
            }
            else -> {
                null
            }
        }
    }

    private fun updateShaderMatrix() {
        val viewBounds = RectF(0f, 0f, width.toFloat(), height.toFloat())
        mShaderMatrix.reset()
        mStokeRect.set(viewBounds)
        when (scaleType) {
            ScaleType.CENTER -> {
                mStokeRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f)
                mShaderMatrix.setTranslate(
                    (mStokeRect.width() - mBitmap!!.width) * 0.5f + 0.5f,
                    (mStokeRect.height() - mBitmap!!.height) * 0.5f + 0.5f
                )
            }
            ScaleType.CENTER_CROP -> {
                var dx = 0f
                var dy = 0f
                val scale: Float
                mStokeRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f)
                if (mBitmap!!.width * mStokeRect.height() > mStokeRect.width() * mBitmap!!.height) {
                    scale = mStokeRect.height() / mBitmap!!.height.toFloat()
                    dx = (mStokeRect.width() - mBitmap!!.width * scale) * 0.5f
                } else {
                    scale = mStokeRect.width() / mBitmap!!.width.toFloat()
                    dy = (mStokeRect.height() - mBitmap!!.height * scale) * 0.5f
                }
                mShaderMatrix.setScale(scale, scale)
                mShaderMatrix.postTranslate(
                    dx + 0.5f + mStrokeWidth / 2f, dy + 0.5f + mStrokeWidth / 2f
                )
            }
            ScaleType.MATRIX,
            ScaleType.FIT_START,
            ScaleType.FIT_END,
            ScaleType.CENTER_INSIDE -> {
                val width = viewBounds.width()
                val height = viewBounds.height()
                val scale = if (mBitmap!!.width <= width && mBitmap!!.height <= height) {
                    1.0f
                } else {
                    min(width / mBitmap!!.width.toFloat(), height / mBitmap!!.height.toFloat())
                }
                val dx = (width - mBitmap!!.width * scale) * 0.5f + 0.5f
                val dy = (height - mBitmap!!.height * scale) * 0.5f + 0.5f
                mShaderMatrix.setScale(scale, scale)
                mShaderMatrix.postTranslate(dx, dy)
                mStokeRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f)
            }
            ScaleType.FIT_CENTER -> {
                mShaderMatrix.mapRect(mStokeRect)
                mStokeRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f)
            }
            ScaleType.FIT_XY -> {
                mStokeRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f)
                mShaderMatrix.setRectToRect(
                    RectF(0f, 0f, mBitmap!!.width.toFloat(), mBitmap!!.height.toFloat()),
                    mStokeRect, ScaleToFit.FILL
                )
            }
            else -> {}
        }
        mBitmapRect.set(mStokeRect)
        if (hasPadding()) {
            mBitmapRect.inset(mStrokeWidth / 2f, mStrokeWidth / 2f)
            mBitmapRect.left += paddingStart
            mBitmapRect.top += paddingTop
            mBitmapRect.right -= paddingEnd
            mBitmapRect.bottom -= paddingBottom
        }
    }

    private fun hasPadding(): Boolean {
        return paddingStart != 0 || paddingTop != 0 || paddingEnd != 0 || paddingBottom != 0
    }

    private fun cornerHasRadius(): Boolean {
        return mLeftTopRadius > 0 || mLeftBottomRadius > 0 || mRightTopRadius > 0 || mRightBottomRadius > 0
    }

    private fun setRoundPath(path: Path, rect: RectF) {
        path.reset()
        path.addRoundRect(
            rect, floatArrayOf(
                mLeftTopRadius, mLeftTopRadius, mRightTopRadius, mRightTopRadius,
                mRightBottomRadius, mRightBottomRadius, mLeftBottomRadius, mLeftBottomRadius
            ), Path.Direction.CW
        )
    }
}