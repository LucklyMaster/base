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
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
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

    var isCircle: Boolean
        private set
    var strokeColor: Int
        private set
    var strokeWidth: Float
        private set
    var dashWidth: Float
        private set
    var dashGap: Float
        private set
    var radius: Float
        private set
    var leftTopRadius: Float
        private set
    var leftBottomRadius: Float
        private set
    var rightTopRadius: Float
        private set
    var rightBottomRadius: Float
        private set
    var isForceSquare: Boolean
        private set
    var squareBy: Int
        private set

    private val stokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stokeRect = RectF()
    private val bitmapRect = RectF()
    private val shaderMatrix = Matrix()
    private var shader: BitmapShader? = null
    private var bitmap: Bitmap? = null
    private var rebuildShader = true
    private var rebuildMatrix = true
    private val strokePath = Path()
    private val bitmapPath = Path()

    companion object {
        const val SQUARE_WIDTH = 0
        const val SQUARE_HEIGHT = 1
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView)
        isCircle = a.getBoolean(R.styleable.ShapeImageView_mc_isCircle, false)
        strokeWidth = a.getDimension(R.styleable.ShapeImageView_mc_strokeWidth, 0f)
        strokeColor = a.getColor(R.styleable.ShapeImageView_mc_strokeColor, 0)
        dashWidth = a.getDimension(R.styleable.ShapeImageView_mc_dashWidth, 0f)
        dashGap = a.getDimension(R.styleable.ShapeImageView_mc_dashGap, 0f)
        radius = a.getDimension(R.styleable.ShapeImageView_mc_radius, 0f)
        leftTopRadius = a.getDimension(R.styleable.ShapeImageView_mc_leftTopRadius, radius)
        leftBottomRadius = a.getDimension(R.styleable.ShapeImageView_mc_leftBottomRadius, radius)
        rightTopRadius = a.getDimension(R.styleable.ShapeImageView_mc_rightTopRadius, radius)
        rightBottomRadius = a.getDimension(R.styleable.ShapeImageView_mc_rightBottomRadius, radius)
        isForceSquare = a.getBoolean(R.styleable.ShapeImageView_mc_forceSquare, false)
        squareBy = a.getInteger(R.styleable.ShapeImageView_mc_squareBy, SQUARE_WIDTH)
        a.recycle()
        stokePaint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
    }

    override fun setImageResource(resId: Int) {
        rebuildShader = true
        rebuildMatrix = true
        super.setImageResource(resId)
    }

    override fun setImageURI(uri: Uri?) {
        rebuildShader = true
        rebuildMatrix = true
        super.setImageURI(uri)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        rebuildShader = true
        rebuildMatrix = true
        super.setImageDrawable(drawable)
    }

    override fun setImageBitmap(bm: Bitmap) {
        rebuildShader = true
        rebuildMatrix = true
        super.setImageBitmap(bm)
    }

    override fun setScaleType(scaleType: ScaleType) {
        rebuildMatrix = true
        super.setScaleType(scaleType)
    }

    fun setCircle(isCircle: Boolean) = apply {
        this.isCircle = isCircle
        invalidate()
    }

    fun setStrokeWidth(width: Float) = apply {
        this.strokeWidth = width
        invalidate()
    }

    fun setStrokeColor(@ColorInt color: Int) = apply {
        this.strokeColor = color
        invalidate()
    }

    fun setDashWidth(dashWidth: Float) = apply {
        this.dashWidth = dashWidth
        invalidate()
    }

    fun setDashGap(dashGap: Float) = apply {
        this.dashGap = dashGap
        invalidate()
    }

    fun setRadius(radius: Float) = apply {
        this.radius = radius
        invalidate()
    }

    fun setRadius(leftTop: Float, leftBottom: Float, rightTop: Float, rightBottom: Float) = apply {
        this.leftTopRadius = leftTop
        this.leftBottomRadius = leftBottom
        this.rightTopRadius = rightTop
        this.rightBottomRadius = rightBottom
        invalidate()
    }

    fun setForceSquare(isForceSquare: Boolean) = apply {
        this.isForceSquare = isForceSquare
        rebuildMatrix = true
        rebuildShader = true
        requestLayout()
    }

    fun setSquareBy(@IntRange(from = 0, to = 1) squareBy: Int) = apply {
        this.squareBy = squareBy
        rebuildMatrix = true
        rebuildShader = true
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        stokePaint.style = Paint.Style.STROKE
        stokePaint.strokeWidth = strokeWidth
        stokePaint.color = strokeColor

        if (drawable == null) {
            super.onDraw(canvas)
            return
        }

        if (rebuildShader) {
            bitmap = getBitmap(drawable)
            shader = BitmapShader(bitmap!!, TileMode.CLAMP, TileMode.CLAMP)
            rebuildShader = false
        }

        if (rebuildMatrix) {
            updateShaderMatrix()
            shader!!.setLocalMatrix(shaderMatrix)
            bitmapPaint.shader = shader
            rebuildMatrix = false
        }

        if (isCircle) {
            canvas.drawOval(bitmapRect, bitmapPaint)
            if (strokeWidth > 0) {
                canvas.drawOval(stokeRect, stokePaint)
            }
        } else {
            if (cornerHasRadius()) {
                setRoundPath(bitmapPath, bitmapRect)
                canvas.drawPath(bitmapPath, bitmapPaint)
                if (strokeWidth > 0) {
                    setRoundPath(strokePath, stokeRect)
                    canvas.drawPath(strokePath, stokePaint)
                }
            } else {
                canvas.drawRect(bitmapRect, bitmapPaint)
                if (strokeWidth > 0) {
                    canvas.drawRect(stokeRect, stokePaint)
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec
        if (isForceSquare) {
            when (squareBy) {
                SQUARE_WIDTH -> {
                    widthSpec = widthMeasureSpec
                    heightSpec = widthMeasureSpec
                }
                SQUARE_HEIGHT -> {
                    widthSpec = heightMeasureSpec
                    heightSpec = heightMeasureSpec
                }
            }
        }
        super.onMeasure(widthSpec, heightSpec)
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
        shaderMatrix.reset()
        stokeRect.set(viewBounds)
        when (scaleType) {
            ScaleType.CENTER -> {
                stokeRect.inset(strokeWidth / 2f, strokeWidth / 2f)
                shaderMatrix.setTranslate(
                    (stokeRect.width() - bitmap!!.width) * 0.5f + 0.5f,
                    (stokeRect.height() - bitmap!!.height) * 0.5f + 0.5f
                )
            }
            ScaleType.CENTER_CROP -> {
                var dx = 0f
                var dy = 0f
                val scale: Float
                stokeRect.inset(strokeWidth / 2f, strokeWidth / 2f)
                if (bitmap!!.width * stokeRect.height() > stokeRect.width() * bitmap!!.height) {
                    scale = stokeRect.height() / bitmap!!.height.toFloat()
                    dx = (stokeRect.width() - bitmap!!.width * scale) * 0.5f
                } else {
                    scale = stokeRect.width() / bitmap!!.width.toFloat()
                    dy = (stokeRect.height() - bitmap!!.height * scale) * 0.5f
                }
                shaderMatrix.setScale(scale, scale)
                shaderMatrix.postTranslate(
                    dx + 0.5f + strokeWidth / 2f, dy + 0.5f + strokeWidth / 2f
                )
            }
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER_INSIDE -> {
                val width = viewBounds.width()
                val height = viewBounds.height()
                val scale = if (bitmap!!.width <= width && bitmap!!.height <= height) {
                    1.0f
                } else {
                    min(width / bitmap!!.width.toFloat(), height / bitmap!!.height.toFloat())
                }
                val dx = (width - bitmap!!.width * scale) * 0.5f + 0.5f
                val dy = (height - bitmap!!.height * scale) * 0.5f + 0.5f
                shaderMatrix.setScale(scale, scale)
                shaderMatrix.postTranslate(dx, dy)
                stokeRect.inset(strokeWidth / 2f, strokeWidth / 2f)
            }
            ScaleType.MATRIX,
            ScaleType.FIT_START -> {
                stokeRect.inset(strokeWidth / 2f, strokeWidth / 2f)
                shaderMatrix.mapRect(stokeRect)
            }
            ScaleType.FIT_XY -> {
                stokeRect.inset(strokeWidth / 2f, strokeWidth / 2f)
                shaderMatrix.setRectToRect(
                    RectF(0f, 0f, bitmap!!.width.toFloat(), bitmap!!.height.toFloat()),
                    stokeRect, ScaleToFit.FILL
                )
            }
            else -> {}
        }
        bitmapRect.set(stokeRect)
        if (hasPadding()) {
            bitmapRect.inset(strokeWidth / 2f, strokeWidth / 2f)
            bitmapRect.left += paddingStart
            bitmapRect.top += paddingTop
            bitmapRect.right -= paddingEnd
            bitmapRect.bottom -= paddingBottom
        }
    }

    private fun hasPadding(): Boolean {
        return paddingStart != 0 || paddingTop != 0 || paddingEnd != 0 || paddingBottom != 0
    }

    private fun cornerHasRadius(): Boolean {
        return leftTopRadius > 0 || leftBottomRadius > 0 || rightTopRadius > 0 || rightBottomRadius > 0
    }

    private fun setRoundPath(path: Path, rect: RectF) {
        path.reset()
        path.addRoundRect(
            rect, floatArrayOf(
                leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius,
                rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius
            ), Path.Direction.CW
        )
    }
}