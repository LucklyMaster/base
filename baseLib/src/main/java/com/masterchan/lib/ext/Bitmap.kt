package com.masterchan.lib.ext

import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.RenderScript.RSMessageHandler
import android.renderscript.ScriptIntrinsicBlur
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

fun Bitmap.scale(scaleX: Float, scaleY: Float): Bitmap {
    val matrix = Matrix()
    matrix.setScale(scaleX, scaleY)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
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
fun Bitmap.toRound(@FloatRange(from = 0.0) borderSize: Float, @ColorInt borderColor: Int): Bitmap {
    val size = min(width, height)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var rectF = RectF(0f, 0f, width.toFloat(), height.toFloat()).apply {
        inset((width - size) / 2f, (height - size) / 2f)
    }
    var bitmap = this
    if (width != height) {
        bitmap = clip(rectF.toRect())
    }
    paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
    val canvas = Canvas(resultBitmap)
    rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
    canvas.drawRoundRect(rectF, rectF.centerX(), rectF.centerY(), paint)
    if (borderSize > 0) {
        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        val radius = rectF.centerX() - borderSize / 2f
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius, paint)
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

/**
 * 为Bitmap添加倒影
 * @receiver Bitmap
 * @param reflectionHeight 倒影高度
 * @param reflectionStartColor 倒影开始颜色
 * @param reflectionEndColor 倒影结束颜色
 * @param reflectionGap 倒影和原图之间的间距
 * @return Bitmap
 */
fun Bitmap.addReflection(
    reflectionHeight: Int,
    reflectionStartColor: Int = 0x70FFFFFF,
    reflectionEndColor: Int = 0x00FFFFFF,
    reflectionGap: Int = 0
): Bitmap {
    val matrix = Matrix().apply { preScale(1f, -1f) }
    val reflectionBitmap = Bitmap.createBitmap(
        this, 0, height - reflectionHeight, width, reflectionHeight, matrix, false
    )
    val resultBitmap = Bitmap.createBitmap(width, height + reflectionHeight, config)
    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(this, 0f, 0f, null)
    canvas.drawBitmap(reflectionBitmap, 0f, (height + reflectionGap).toFloat(), null)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.shader = LinearGradient(
        0f, height.toFloat(),
        0f, resultBitmap.height.toFloat() + reflectionGap,
        reflectionStartColor,
        reflectionEndColor,
        Shader.TileMode.MIRROR
    )
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    canvas.drawRect(Rect(0, height + reflectionGap, width, resultBitmap.height), paint)
    if (!reflectionBitmap.isRecycled) reflectionBitmap.recycle()
    return resultBitmap
}

/**
 * 添加文字水印
 * @receiver Bitmap
 * @param content 文字内容
 * @param textSize 字体大小
 * @param color 字体颜色
 * @param x 文字坐标
 * @param y 文字坐标
 * @param settings 自定义设置
 * @return Bitmap
 */
fun Bitmap.addTextWatermark(
    content: String,
    textSize: Float,
    @ColorInt color: Int,
    x: Float,
    y: Float,
    settings: (Paint.(canvas: Canvas) -> Unit)? = null
): Bitmap {
    val resultBitmap = copy(config, true)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas(resultBitmap)
    paint.color = color
    paint.textSize = textSize
    settings?.invoke(paint, canvas)
    canvas.drawText(content, x, y + textSize, paint)
    return resultBitmap
}

/**
 * 添加图片水印
 * @receiver Bitmap
 * @param watermark Bitmap
 * @param x Int
 * @param y Int
 * @param settings 自定义设置
 * @return Bitmap?
 */
fun Bitmap.addImageWatermark(
    watermark: Bitmap,
    x: Int,
    y: Int,
    settings: (Paint.(canvas: Canvas) -> Unit)? = null
): Bitmap? {
    val resultBitmap = copy(config, true)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val canvas = Canvas(resultBitmap)
    settings?.invoke(paint, canvas)
    canvas.drawBitmap(watermark, x.toFloat(), y.toFloat(), paint)
    return resultBitmap
}

/**
 * 高斯模糊
 * @receiver Bitmap
 * @param scale 缩放比例，图像缩放->高斯模糊->缩放回原图大小
 * @param radius 高斯模糊半径
 * @return Bitmap
 */
@Suppress("DEPRECATION")
fun Bitmap.toBlur(
    @FloatRange(from = 0.0, to = 1.0, fromInclusive = false) scale: Float,
    @FloatRange(from = 0.0, to = 25.0, fromInclusive = false) radius: Float,
): Bitmap {
    val scaleBitmap = scale(scale, scale)
    var renderScript: RenderScript? = null
    try {
        renderScript = RenderScript.create(application)
        renderScript.messageHandler = RSMessageHandler()
        val input = Allocation.createFromBitmap(
            renderScript, scaleBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT
        )
        val output = Allocation.createTyped(renderScript, input.type)
        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        blurScript.setInput(input)
        blurScript.setRadius(radius)
        blurScript.forEach(output)
        output.copyTo(scaleBitmap)
    } finally {
        renderScript?.destroy()
    }
    return scaleBitmap.scale(width, height)
}

