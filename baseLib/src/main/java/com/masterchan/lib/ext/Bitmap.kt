package com.masterchan.lib.ext

import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.toRect
import java.io.ByteArrayOutputStream
import kotlin.math.min

fun Bitmap.toByteArray(format: CompressFormat = CompressFormat.PNG, quality: Int = 100): ByteArray {
    val bos = ByteArrayOutputStream()
    compress(format, quality, bos)
    return bos.toByteArray()
}

fun ByteArray.toBitmap(options: BitmapFactory.Options? = null): Bitmap {
    return if (options == null) {
        BitmapFactory.decodeByteArray(this, 0, size)
    } else {
        BitmapFactory.decodeByteArray(this, 0, size, options)
    }
}

fun Bitmap.scale(width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, width, height, true)
}

fun Bitmap.clip(rect: Rect): Bitmap {
    return Bitmap.createBitmap(this, rect.left, rect.top, rect.width(), rect.height())
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.setRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true);
}

/**
 * 错切
 * @receiver Bitmap
 * @param kx Float
 * @param ky Float
 * @param px Float
 * @param py Float
 * @return Bitmap
 */
fun Bitmap.skew(kx: Float, ky: Float, px: Float, py: Float): Bitmap {
    val matrix = Matrix()
    matrix.setSkew(kx, ky, px, py)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

/**
 * 转换成圆形Bitmap
 * @receiver Bitmap
 * @param borderSize border宽度
 * @param borderColor border颜色
 * @return Bitmap?
 */
fun Bitmap.toRound(@FloatRange(from = 0.0) borderSize: Float, @ColorInt borderColor: Int): Bitmap? {
    val size = min(width, height)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val resultBitmap = Bitmap.createBitmap(width, height, config)
    val center = size / 2f
    val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat()).apply {
        inset((width - size) / 2f, (height - size) / 2f)
    }
    val matrix = Matrix()
    matrix.setTranslate(rectF.left, rectF.top)
    var bitmap = this
    if (width != height) {
        bitmap = clip(rectF.toRect())
        // matrix.preScale(size.toFloat() / width, size.toFloat() / height)
    }
    paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
        setLocalMatrix(matrix)
    }
    val canvas = Canvas(resultBitmap)
    canvas.drawRoundRect(rectF, center, center, paint)
    if (borderSize > 0) {
        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        val radius = center - borderSize / 2f
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }
    return resultBitmap
}

fun Bitmap.toRoundCorner(
    radius: Float,
    @FloatRange(from = 0.0) borderSize: Float,
    @ColorInt borderColor: Int,
): Bitmap {
    return toRoundCorner(FloatArray(8) { radius }, borderSize, borderColor)
}

/**
 * 转换成圆角矩形
 * @receiver Bitmap
 * @param radii 每个角的半径:floatArrayOf
 * (leftTopRadius, leftTopRadius,
 * rightTopRadius, rightTopRadius,
 * rightBottomRadius,rightBottomRadius,
 * leftBottomRadius, leftBottomRadius)
 * @param borderSize border宽度
 * @param borderColor border颜色
 * @return Bitmap
 */
fun Bitmap.toRoundCorner(
    radii: FloatArray,
    @FloatRange(from = 0.0) borderSize: Float,
    @ColorInt borderColor: Int,
): Bitmap {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val resultBitmap = Bitmap.createBitmap(width, height, config)
    paint.shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    val canvas = Canvas(resultBitmap)
    val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat()).apply {
        inset(borderSize / 2f, borderSize / 2f)
    }
    val path = Path()
    path.addRoundRect(rectF, radii, Path.Direction.CW)
    canvas.drawPath(path, paint)
    if (borderSize > 0) {
        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawPath(path, paint)
    }
    return resultBitmap
}
