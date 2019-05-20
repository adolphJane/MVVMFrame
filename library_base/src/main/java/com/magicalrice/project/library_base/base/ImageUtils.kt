package com.magicalrice.project.library_base.base

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.magicalrice.project.library_base.base.log.LogUtils
import java.io.*

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 图片相关
 *
 * bitmap2Bytes, bytes2Bitmap      : bitmap 与 bytes 互转
 * drawable2Bitmap, bitmap2Drawable: drawable 与 bitmap 互转
 * drawable2Bytes, bytes2Drawable  : drawable 与 bytes 互转
 * view2Bitmap                     : view 转 bitmap
 * getBitmap                       : 获取 bitmap
 * scale                           : 缩放图片
 * clip                            : 裁剪图片
 * skew                            : 倾斜图片
 * rotate                          : 旋转图片
 * getRotateDegree                 : 获取图片旋转角度
 * toRound                         : 转为圆形图片
 * toRoundCorner                   : 转为圆角图片
 * addCornerBorder                 : 添加圆角边框
 * addCircleBorder                 : 添加圆形边框
 * addReflection                   : 添加倒影
 * addTextWatermark                : 添加文字水印
 * addImageWatermark               : 添加图片水印
 * toAlpha                         : 转为 alpha 位图
 * toGray                          : 转为灰度图片
 * fastBlur                        : 快速模糊
 * renderScriptBlur                : renderScript 模糊图片
 * stackBlur                       : stack 模糊图片
 * save                            : 保存图片
 * isImage                         : 根据文件名判断文件是否为图片
 * getImageType                    : 获取图片类型
 * compressByScale                 : 按缩放压缩
 * compressByQuality               : 按质量压缩
 * compressBySampleSize            : 按采样大小压缩
 * getSize                         : 获取图片尺寸
 */

object ImageUtils {
    /**
     * Bitmap 转 bytes.
     *
     * @param bitmap The bitmap.
     * @param format The format of bitmap.
     * @return bytes
     */
    fun bitmap2Bytes(bitmap: Bitmap?, format: Bitmap.CompressFormat): ByteArray? {
        if (bitmap == null) return null
        val baos = ByteArrayOutputStream()
        bitmap.compress(format, 100, baos)
        return baos.toByteArray()
    }

    /**
     * Bytes 转 bitmap.
     *
     * @param bytes The bytes.
     * @return bitmap
     */
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes == null || bytes.size == 0)
            null
        else
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * Drawable 转 bitmap.
     *
     * @param drawable The drawable.
     * @return bitmap
     */
    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(
                1, 1,
                if (drawable.opacity != PixelFormat.OPAQUE)
                    Bitmap.Config.ARGB_8888
                else
                    Bitmap.Config.RGB_565
            )
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE)
                    Bitmap.Config.ARGB_8888
                else
                    Bitmap.Config.RGB_565
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Bitmap 转 drawable.
     *
     * @param bitmap The bitmap.
     * @return drawable
     */
    fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
        return if (bitmap == null) null else BitmapDrawable(
            AppManager.getInstance().getApp().getResources(),
            bitmap
        )
    }

    /**
     * Drawable 转 bytes.
     *
     * @param drawable The drawable.
     * @param format   The format of bitmap.
     * @return bytes
     */
    fun drawable2Bytes(drawable: Drawable?, format: Bitmap.CompressFormat): ByteArray? {
        return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format)
    }

    /**
     * Bytes 转 drawable.
     *
     * @param bytes The bytes.
     * @return drawable
     */
    fun bytes2Drawable(bytes: ByteArray): Drawable? {
        return bitmap2Drawable(bytes2Bitmap(bytes))
    }

    /**
     * View 转 bitmap.
     *
     * @param view The view.
     * @return bitmap
     */
    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val ret = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(ret)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return ret
    }

    /**
     * 饱和度处理
     *
     * @param bitmap          原图
     * @param saturationValue 新的饱和度值
     * @return 改变了饱和度值之后的图片
     */
    fun saturation(bitmap: Bitmap, saturationValue: Int): Bitmap {
        // 计算出符合要求的饱和度值
        val newSaturationValue = saturationValue * 1.0f / 127
        // 创建一个颜色矩阵
        val saturationColorMatrix = ColorMatrix()
        // 设置饱和度值
        saturationColorMatrix.setSaturation(newSaturationValue)
        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(saturationColorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 亮度处理
     *
     * @param bitmap   原图
     * @param lumValue 新的亮度值
     * @return 改变了亮度值之后的图片
     */
    fun lum(bitmap: Bitmap, lumValue: Int): Bitmap {
        // 计算出符合要求的亮度值
        val newlumValue = lumValue * 1.0f / 127
        // 创建一个颜色矩阵
        val lumColorMatrix = ColorMatrix()
        // 设置亮度值
        lumColorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1f)
        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(lumColorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 色相处理
     *
     * @param bitmap   原图
     * @param hueValue 新的色相值
     * @return 改变了色相值之后的图片
     */
    fun hue(bitmap: Bitmap, hueValue: Int): Bitmap {
        // 计算出符合要求的色相值
        val newHueValue = (hueValue - 127) * 1.0f / 127 * 180
        // 创建一个颜色矩阵
        val hueColorMatrix = ColorMatrix()
        // 控制让红色区在色轮上旋转的角度
        hueColorMatrix.setRotate(0, newHueValue)
        // 控制让绿红色区在色轮上旋转的角度
        hueColorMatrix.setRotate(1, newHueValue)
        // 控制让蓝色区在色轮上旋转的角度
        hueColorMatrix.setRotate(2, newHueValue)
        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(hueColorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 亮度、色相、饱和度处理
     *
     * @param bitmap          原图
     * @param lumValue        亮度值
     * @param hueValue        色相值
     * @param saturationValue 饱和度值
     * @return 亮度、色相、饱和度处理后的图片
     */
    fun lumAndHueAndSaturation(
        bitmap: Bitmap, lumValue: Int,
        hueValue: Int, saturationValue: Int
    ): Bitmap {
        // 计算出符合要求的饱和度值
        val newSaturationValue = saturationValue * 1.0f / 127
        // 计算出符合要求的亮度值
        val newlumValue = lumValue * 1.0f / 127
        // 计算出符合要求的色相值
        val newHueValue = (hueValue - 127) * 1.0f / 127 * 180

        // 创建一个颜色矩阵并设置其饱和度
        val colorMatrix = ColorMatrix()

        // 设置饱和度值
        colorMatrix.setSaturation(newSaturationValue)
        // 设置亮度值
        colorMatrix.setScale(newlumValue, newlumValue, newlumValue, 1f)
        // 控制让红色区在色轮上旋转的角度
        colorMatrix.setRotate(0, newHueValue)
        // 控制让绿红色区在色轮上旋转的角度
        colorMatrix.setRotate(1, newHueValue)
        // 控制让蓝色区在色轮上旋转的角度
        colorMatrix.setRotate(2, newHueValue)

        // 创建一个画笔并设置其颜色过滤器
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        // 创建一个新的图片并创建画布
        val newBitmap = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBitmap)
        // 将原图使用给定的画笔画到画布上
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return newBitmap
    }

    /**
     * 怀旧效果处理
     *
     * @param bitmap 原图
     * @return 怀旧效果处理后的图片
     */
    fun nostalgic(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )
        var pixColor = 0
        var pixR = 0
        var pixG = 0
        var pixB = 0
        var newR = 0
        var newG = 0
        var newB = 0
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in 0 until height) {
            for (k in 0 until width) {
                pixColor = pixels[width * i + k]
                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)
                newR = (0.393 * pixR + 0.769 * pixG + 0.189 * pixB).toInt()
                newG = (0.349 * pixR + 0.686 * pixG + 0.168 * pixB).toInt()
                newB = (0.272 * pixR + 0.534 * pixG + 0.131 * pixB).toInt()
                val newColor = Color.argb(
                    255, if (newR > 255) 255 else newR,
                    if (newG > 255) 255 else newG, if (newB > 255) 255 else newB
                )
                pixels[width * i + k] = newColor
            }
        }
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 柔化效果处理
     *
     * @param bitmap 原图
     * @return 柔化效果处理后的图片
     */
    fun soften(bitmap: Bitmap): Bitmap {
        // 高斯矩阵
        val gauss = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)

        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        val delta = 16 // 值越小图片会越亮，越大则越暗

        var idx = 0
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + m) * width + k + n]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)

                        newR = newR + pixR * gauss[idx]
                        newG = newG + pixG * gauss[idx]
                        newB = newB + pixB * gauss[idx]
                        idx++
                    }
                }

                newR /= delta
                newG /= delta
                newB /= delta

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[i * width + k] = Color.argb(255, newR, newG, newB)

                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 光照效果处理
     *
     * @param bitmap  原图
     * @param centerX 光源在X轴的位置
     * @param centerY 光源在Y轴的位置
     * @return 光照效果处理后的图片
     */
    fun sunshine(bitmap: Bitmap, centerX: Int, centerY: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0
        val radius = Math.min(centerX, centerY)

        val strength = 150f // 光照强度 100~150
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var pos = 0
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                pos = i * width + k
                pixColor = pixels[pos]

                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)

                newR = pixR
                newG = pixG
                newB = pixB

                // 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
                val distance = (Math.pow((centerY - i).toDouble(), 2.0) + Math.pow(
                    (centerX - k).toDouble(), 2.0
                )).toInt()
                if (distance < radius * radius) {
                    // 按照距离大小计算增加的光照值
                    val result =
                        (strength * (1.0 - Math.sqrt(distance.toDouble()) / radius)).toInt()
                    newR = pixR + result
                    newG = pixG + result
                    newB = pixB + result
                }

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[pos] = Color.argb(255, newR, newG, newB)
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 底片效果处理
     *
     * @param bitmap 原图
     * @return 底片效果处理后的图片
     */
    fun film(bitmap: Bitmap): Bitmap {
        // RGBA的最大值
        val MAX_VALUE = 255
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var pos = 0
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                pos = i * width + k
                pixColor = pixels[pos]

                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)

                newR = MAX_VALUE - pixR
                newG = MAX_VALUE - pixG
                newB = MAX_VALUE - pixB

                newR = Math.min(MAX_VALUE, Math.max(0, newR))
                newG = Math.min(MAX_VALUE, Math.max(0, newG))
                newB = Math.min(MAX_VALUE, Math.max(0, newB))

                pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB)
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 锐化效果处理
     *
     * @param bitmap 原图
     * @return 锐化效果处理后的图片
     */
    fun sharpen(bitmap: Bitmap): Bitmap {
        // 拉普拉斯矩阵
        val laplacian = intArrayOf(-1, -1, -1, -1, 9, -1, -1, -1, -1)

        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        var idx = 0
        val alpha = 0.3f
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + n) * width + k + m]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)

                        newR = newR + (pixR.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        newG = newG + (pixG.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        newB = newB + (pixB.toFloat() * laplacian[idx].toFloat() * alpha).toInt()
                        idx++
                    }
                }

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[i * width + k] = Color.argb(255, newR, newG, newB)
                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 浮雕效果处理
     *
     * @param bitmap 原图
     * @return 浮雕效果处理后的图片
     */
    fun emboss(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newBitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )

        var pixR = 0
        var pixG = 0
        var pixB = 0

        var pixColor = 0

        var newR = 0
        var newG = 0
        var newB = 0

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        var pos = 0
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                pos = i * width + k
                pixColor = pixels[pos]

                pixR = Color.red(pixColor)
                pixG = Color.green(pixColor)
                pixB = Color.blue(pixColor)

                pixColor = pixels[pos + 1]
                newR = Color.red(pixColor) - pixR + 127
                newG = Color.green(pixColor) - pixG + 127
                newB = Color.blue(pixColor) - pixB + 127

                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))

                pixels[pos] = Color.argb(255, newR, newG, newB)
                k++
            }
            i++
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBitmap
    }

    /**
     * 把一个View的对象转换成bitmap
     *
     * @param view View
     * @return Bitmap
     */
    fun getBitmapFromView2(view: View): Bitmap? {

        view.clearFocus()
        view.isPressed = false

        // 能画缓存就返回false
        val willNotCache = view.willNotCacheDrawing()
        view.setWillNotCacheDrawing(false)
        val color = view.drawingCacheBackgroundColor
        view.drawingCacheBackgroundColor = 0
        if (color != 0) {
            view.destroyDrawingCache()
        }
        view.buildDrawingCache()
        val cacheBitmap = view.drawingCache
        if (cacheBitmap == null) {
            LogUtils.e("getBitmapFromView2", "failed getViewBitmap($view)", RuntimeException())
            return null
        }
        val bitmap = Bitmap.createBitmap(cacheBitmap)
        // Restore the view
        view.destroyDrawingCache()
        view.setWillNotCacheDrawing(willNotCache)
        view.drawingCacheBackgroundColor = color
        return bitmap
    }

    /**
     * 获取 bitmap
     *
     * @param file The file.
     * @return bitmap
     */
    fun getBitmap(file: File?): Bitmap? {
        return if (file == null) null else BitmapFactory.decodeFile(file.absolutePath)
    }

    /**
     * 获取 bitmap
     *
     * @param file      The file.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    fun getBitmap(file: File?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (file == null) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    /**
     * 获取 bitmap
     *
     * @param filePath The path of file.
     * @return bitmap
     */
    fun getBitmap(filePath: String): Bitmap? {
        return if (isSpace(filePath)) null else BitmapFactory.decodeFile(filePath)
    }

    /**
     * 获取 bitmap
     *
     * @param filePath  The path of file.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    fun getBitmap(filePath: String, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (isSpace(filePath)) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 获取 bitmap
     *
     * @param is The input stream.
     * @return bitmap
     */
    fun getBitmap(`is`: InputStream?): Bitmap? {
        return if (`is` == null) null else BitmapFactory.decodeStream(`is`)
    }

    /**
     * 获取 bitmap
     *
     * @param is        The input stream.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    fun getBitmap(`is`: InputStream?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (`is` == null) return null
        val bytes = input2Byte(`is`)
        return getBitmap(bytes!!, 0, maxWidth, maxHeight)
    }

    /**
     * 获取 bitmap
     *
     * @param data   The data.
     * @param offset The offset.
     * @return bitmap
     */
    fun getBitmap(data: ByteArray, offset: Int): Bitmap? {
        return if (data.size == 0) null else BitmapFactory.decodeByteArray(data, offset, data.size)
    }

    /**
     * 获取 bitmap
     *
     * @param data      The data.
     * @param offset    The offset.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    fun getBitmap(
        data: ByteArray,
        offset: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        if (data.size == 0) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, offset, data.size, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(data, offset, data.size, options)
    }

    /**
     * 获取 bitmap
     *
     * @param resId The resource id.
     * @return bitmap
     */
    fun getBitmap(@DrawableRes resId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(AppManager.getInstance().getApp(), resId)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 获取 bitmap
     *
     * @param resId     The resource id.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    fun getBitmap(
        @DrawableRes resId: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap {
        val options = BitmapFactory.Options()
        val resources = AppManager.getInstance().getApp().getResources()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, resId, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources, resId, options)
    }

    /**
     * 获取 bitmap
     *
     * @param fd The file descriptor.
     * @return bitmap
     */
    fun getBitmap(fd: FileDescriptor?): Bitmap? {
        return if (fd == null) null else BitmapFactory.decodeFileDescriptor(fd)
    }

    /**
     * 获取 bitmap
     *
     * @param fd        The file descriptor
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    fun getBitmap(
        fd: FileDescriptor?,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        if (fd == null) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(fd, null, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFileDescriptor(fd, null, options)
    }

    /**
     * Return the bitmap with the specified color.
     *
     * @param src   The source of bitmap.
     * @param color The color.
     * @return the bitmap with the specified color
     */
    fun drawColor(src: Bitmap, @ColorInt color: Int): Bitmap? {
        return drawColor(src, color, false)
    }

    /**
     * Return the bitmap with the specified color.
     *
     * @param src     The source of bitmap.
     * @param color   The color.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with the specified color
     */
    fun drawColor(
        src: Bitmap,
        @ColorInt color: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = if (recycle) src else src.copy(src.config, true)
        val canvas = Canvas(ret)
        canvas.drawColor(color, PorterDuff.Mode.DARKEN)
        return ret
    }

    /**
     * 缩放图片
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @return the scaled bitmap
     */
    fun scale(src: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        return scale(src, newWidth, newHeight, false)
    }

    /**
     * 缩放图片
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the scaled bitmap
     */
    fun scale(
        src: Bitmap,
        newWidth: Int,
        newHeight: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 缩放图片
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @return the scaled bitmap
     */
    fun scale(src: Bitmap, scaleWidth: Float, scaleHeight: Float): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, false)
    }

    /**
     * 缩放图片
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return the scaled bitmap
     */
    fun scale(
        src: Bitmap,
        scaleWidth: Float,
        scaleHeight: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setScale(scaleWidth, scaleHeight)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 裁剪图片
     *
     * @param src    The source of bitmap.
     * @param x      The x coordinate of the first pixel.
     * @param y      The y coordinate of the first pixel.
     * @param width  The width.
     * @param height The height.
     * @return the clipped bitmap
     */
    fun clip(
        src: Bitmap,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ): Bitmap? {
        return clip(src, x, y, width, height, false)
    }

    /**
     * 裁剪图片
     *
     * @param src     The source of bitmap.
     * @param x       The x coordinate of the first pixel.
     * @param y       The y coordinate of the first pixel.
     * @param width   The width.
     * @param height  The height.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the clipped bitmap
     */
    fun clip(
        src: Bitmap,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createBitmap(src, x, y, width, height)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 倾斜图片
     *
     * @param src The source of bitmap.
     * @param kx  The skew factor of x.
     * @param ky  The skew factor of y.
     * @return the skewed bitmap
     */
    fun skew(src: Bitmap, kx: Float, ky: Float): Bitmap? {
        return skew(src, kx, ky, 0f, 0f, false)
    }

    /**
     * 倾斜图片
     *
     * @param src     The source of bitmap.
     * @param kx      The skew factor of x.
     * @param ky      The skew factor of y.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the skewed bitmap
     */
    fun skew(
        src: Bitmap,
        kx: Float,
        ky: Float,
        recycle: Boolean
    ): Bitmap? {
        return skew(src, kx, ky, 0f, 0f, recycle)
    }

    /**
     * 倾斜图片
     *
     * @param src The source of bitmap.
     * @param kx  The skew factor of x.
     * @param ky  The skew factor of y.
     * @param px  The x coordinate of the pivot point.
     * @param py  The y coordinate of the pivot point.
     * @return the skewed bitmap
     */
    fun skew(
        src: Bitmap,
        kx: Float,
        ky: Float,
        px: Float,
        py: Float
    ): Bitmap? {
        return skew(src, kx, ky, px, py, false)
    }

    /**
     * 倾斜图片
     *
     * @param src     The source of bitmap.
     * @param kx      The skew factor of x.
     * @param ky      The skew factor of y.
     * @param px      The x coordinate of the pivot point.
     * @param py      The y coordinate of the pivot point.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the skewed bitmap
     */
    fun skew(
        src: Bitmap,
        kx: Float,
        ky: Float,
        px: Float,
        py: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val matrix = Matrix()
        matrix.setSkew(kx, ky, px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 旋转图片
     *
     * @param src     The source of bitmap.
     * @param degrees The number of degrees.
     * @param px      The x coordinate of the pivot point.
     * @param py      The y coordinate of the pivot point.
     * @return the rotated bitmap
     */
    fun rotate(
        src: Bitmap,
        degrees: Int,
        px: Float,
        py: Float
    ): Bitmap? {
        return rotate(src, degrees, px, py, false)
    }

    /**
     * 旋转图片
     *
     * @param src     The source of bitmap.
     * @param degrees The number of degrees.
     * @param px      The x coordinate of the pivot point.
     * @param py      The y coordinate of the pivot point.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the rotated bitmap
     */
    fun rotate(
        src: Bitmap,
        degrees: Int,
        px: Float,
        py: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        if (degrees == 0) return src
        val matrix = Matrix()
        matrix.setRotate(degrees.toFloat(), px, py)
        val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 获取图片旋转角度
     *
     * @param filePath The path of file.
     * @return the rotated degree
     */
    fun getRotateDegree(filePath: String): Int {
        try {
            val exifInterface = ExifInterface(filePath)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
                else -> return 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }

    }

    /**
     * 转为圆形图片
     *
     * @param src The source of bitmap.
     * @return the round bitmap
     */
    fun toRound(src: Bitmap): Bitmap? {
        return toRound(src, 0, 0, false)
    }

    /**
     * 转为圆形图片
     *
     * @param src     The source of bitmap.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the round bitmap
     */
    fun toRound(src: Bitmap, recycle: Boolean): Bitmap? {
        return toRound(src, 0, 0, recycle)
    }

    /**
     * 转为圆形图片
     *
     * @param src         The source of bitmap.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @return the round bitmap
     */
    fun toRound(
        src: Bitmap,
        @IntRange(from = 0) borderSize: Int,
        @ColorInt borderColor: Int
    ): Bitmap? {
        return toRound(src, borderSize, borderColor, false)
    }

    /**
     * 转为圆形图片
     *
     * @param src         The source of bitmap.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @return the round bitmap
     */
    fun toRound(
        src: Bitmap,
        @IntRange(from = 0) borderSize: Int,
        @ColorInt borderColor: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val size = Math.min(width, height)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, src.config)
        val center = size / 2f
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        rectF.inset((width - size) / 2f, (height - size) / 2f)
        val matrix = Matrix()
        matrix.setTranslate(rectF.left, rectF.top)
        matrix.preScale(size.toFloat() / width, size.toFloat() / height)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(matrix)
        paint.shader = shader
        val canvas = Canvas(ret)
        canvas.drawRoundRect(rectF, center, center, paint)
        if (borderSize > 0) {
            paint.shader = null
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize.toFloat()
            val radius = center - borderSize / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        }
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 转为圆角图片
     *
     * @param src    The source of bitmap.
     * @param radius The radius of corner.
     * @return the round corner bitmap
     */
    fun toRoundCorner(src: Bitmap, radius: Float): Bitmap? {
        return toRoundCorner(src, radius, 0, 0, false)
    }

    /**
     * 转为圆角图片
     *
     * @param src     The source of bitmap.
     * @param radius  The radius of corner.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the round corner bitmap
     */
    fun toRoundCorner(
        src: Bitmap,
        radius: Float,
        recycle: Boolean
    ): Bitmap? {
        return toRoundCorner(src, radius, 0, 0, recycle)
    }

    /**
     * 转为圆角图片
     *
     * @param src         The source of bitmap.
     * @param radius      The radius of corner.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @return the round corner bitmap
     */
    fun toRoundCorner(
        src: Bitmap,
        radius: Float,
        @IntRange(from = 0) borderSize: Int,
        @ColorInt borderColor: Int
    ): Bitmap? {
        return toRoundCorner(src, radius, borderSize, borderColor, false)
    }

    /**
     * 转为圆角图片
     *
     * @param src         The source of bitmap.
     * @param radius      The radius of corner.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return the round corner bitmap
     */
    fun toRoundCorner(
        src: Bitmap,
        radius: Float,
        @IntRange(from = 0) borderSize: Int,
        @ColorInt borderColor: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val ret = Bitmap.createBitmap(width, height, src.config)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        val canvas = Canvas(ret)
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val halfBorderSize = borderSize / 2f
        rectF.inset(halfBorderSize, halfBorderSize)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        if (borderSize > 0) {
            paint.shader = null
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize.toFloat()
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawRoundRect(rectF, radius, radius, paint)
        }
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 添加圆角边框
     *
     * @param src          The source of bitmap.
     * @param borderSize   The size of border.
     * @param color        The color of border.
     * @param cornerRadius The radius of corner.
     * @return the round corner bitmap with border
     */
    fun addCornerBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, cornerRadius, false)
    }

    /**
     * 水平翻转处理
     *
     * @param bitmap 原图
     * @return 水平翻转后的图片
     */
    fun reverseByHorizontal(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1f, 1f)
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, false
        )
    }

    /**
     * 垂直翻转处理
     *
     * @param bitmap 原图
     * @return 垂直翻转后的图片
     */
    fun reverseByVertical(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        return Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, false
        )
    }

    /**
     * 更改图片色系，变亮或变暗
     *
     * @param delta 图片的亮暗程度值，越小图片会越亮，取值范围(0,24)
     * @return
     */
    fun adjustTone(src: Bitmap, delta: Int): Bitmap? {
        if (delta >= 24 || delta <= 0) {
            return null
        }
        // 设置高斯矩阵
        val gauss = intArrayOf(1, 2, 1, 2, 4, 2, 1, 2, 1)
        val width = src.width
        val height = src.height
        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.RGB_565
        )

        var pixR = 0
        var pixG = 0
        var pixB = 0
        var pixColor = 0
        var newR = 0
        var newG = 0
        var newB = 0
        var idx = 0
        val pixels = IntArray(width * height)

        src.getPixels(pixels, 0, width, 0, 0, width, height)
        var i = 1
        val length = height - 1
        while (i < length) {
            var k = 1
            val len = width - 1
            while (k < len) {
                idx = 0
                for (m in -1..1) {
                    for (n in -1..1) {
                        pixColor = pixels[(i + m) * width + k + n]
                        pixR = Color.red(pixColor)
                        pixG = Color.green(pixColor)
                        pixB = Color.blue(pixColor)

                        newR += pixR * gauss[idx]
                        newG += pixG * gauss[idx]
                        newB += pixB * gauss[idx]
                        idx++
                    }
                }
                newR /= delta
                newG /= delta
                newB /= delta
                newR = Math.min(255, Math.max(0, newR))
                newG = Math.min(255, Math.max(0, newG))
                newB = Math.min(255, Math.max(0, newB))
                pixels[i * width + k] = Color.argb(255, newR, newG, newB)
                newR = 0
                newG = 0
                newB = 0
                k++
            }
            i++
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    /**
     * 将彩色图转换为黑白图
     *
     * @param bmp 位图
     * @return 返回转换好的位图
     */
    fun convertToBlackWhite(bmp: Bitmap): Bitmap {
        val width = bmp.width
        val height = bmp.height
        val pixels = IntArray(width * height)
        bmp.getPixels(pixels, 0, width, 0, 0, width, height)

        val alpha = 0xFF shl 24 // 默认将bitmap当成24色图片
        for (i in 0 until height) {
            for (j in 0 until width) {
                var grey = pixels[width * i + j]

                val red = grey and 0x00FF0000 shr 16
                val green = grey and 0x0000FF00 shr 8
                val blue = grey and 0x000000FF

                grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                grey = alpha or (grey shl 16) or (grey shl 8) or grey
                pixels[width * i + j] = grey
            }
        }
        val newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height)
        return newBmp
    }

    /**
     * Return the round corner bitmap with border.
     *
     * @param src          The source of bitmap.
     * @param borderSize   The size of border.
     * @param color        The color of border.
     * @param cornerRadius The radius of corner.
     * @param recycle      True to recycle the source of bitmap, false otherwise.
     * @return the round corner bitmap with border
     */
    fun addCornerBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        @FloatRange(from = 0.0) cornerRadius: Float,
        recycle: Boolean
    ): Bitmap? {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle)
    }

    /**
     * Return the round bitmap with border.
     *
     * @param src        The source of bitmap.
     * @param borderSize The size of border.
     * @param color      The color of border.
     * @return the round bitmap with border
     */
    fun addCircleBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int
    ): Bitmap? {
        return addBorder(src, borderSize, color, true, 0f, false)
    }

    /**
     * Return the round bitmap with border.
     *
     * @param src        The source of bitmap.
     * @param borderSize The size of border.
     * @param color      The color of border.
     * @param recycle    True to recycle the source of bitmap, false otherwise.
     * @return the round bitmap with border
     */
    fun addCircleBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        recycle: Boolean
    ): Bitmap? {
        return addBorder(src, borderSize, color, true, 0f, recycle)
    }

    /**
     * Return the bitmap with border.
     *
     * @param src          The source of bitmap.
     * @param borderSize   The size of border.
     * @param color        The color of border.
     * @param isCircle     True to draw circle, false to draw corner.
     * @param cornerRadius The radius of corner.
     * @param recycle      True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with border
     */
    private fun addBorder(
        src: Bitmap,
        @IntRange(from = 1) borderSize: Int,
        @ColorInt color: Int,
        isCircle: Boolean,
        cornerRadius: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = if (recycle) src else src.copy(src.config, true)
        val width = ret.width
        val height = ret.height
        val canvas = Canvas(ret)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize.toFloat()
        if (isCircle) {
            val radius = Math.min(width, height) / 2f - borderSize / 2f
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        } else {
            val halfBorderSize = borderSize shr 1
            val rectF = RectF(
                halfBorderSize.toFloat(), halfBorderSize.toFloat(),
                (width - halfBorderSize).toFloat(), (height - halfBorderSize).toFloat()
            )
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        }
        return ret
    }

    /**
     * 添加倒影
     *
     * @param src              The source of bitmap.
     * @param reflectionHeight The height of reflection.
     * @return the bitmap with reflection
     */
    fun addReflection(src: Bitmap, reflectionHeight: Int): Bitmap? {
        return addReflection(src, reflectionHeight, false)
    }

    /**
     * 添加倒影
     *
     * @param src              The source of bitmap.
     * @param reflectionHeight The height of reflection.
     * @param recycle          True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with reflection
     */
    fun addReflection(
        src: Bitmap,
        reflectionHeight: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val REFLECTION_GAP = 0
        val srcWidth = src.width
        val srcHeight = src.height
        val matrix = Matrix()
        matrix.preScale(1f, -1f)
        val reflectionBitmap = Bitmap.createBitmap(
            src, 0, srcHeight - reflectionHeight,
            srcWidth, reflectionHeight, matrix, false
        )
        val ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.config)
        val canvas = Canvas(ret)
        canvas.drawBitmap(src, 0f, 0f, null)
        canvas.drawBitmap(reflectionBitmap, 0f, (srcHeight + REFLECTION_GAP).toFloat(), null)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val shader = LinearGradient(
            0f, srcHeight.toFloat(),
            0f, (ret.height + REFLECTION_GAP).toFloat(),
            0x70FFFFFF,
            0x00FFFFFF,
            Shader.TileMode.MIRROR
        )
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawRect(
            0f,
            (srcHeight + REFLECTION_GAP).toFloat(),
            srcWidth.toFloat(),
            ret.height.toFloat(),
            paint
        )
        if (!reflectionBitmap.isRecycled) reflectionBitmap.recycle()
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 添加文字水印
     *
     * @param src      The source of bitmap.
     * @param content  The content of text.
     * @param textSize The size of text.
     * @param color    The color of text.
     * @param x        The x coordinate of the first pixel.
     * @param y        The y coordinate of the first pixel.
     * @return the bitmap with text watermarking
     */
    fun addTextWatermark(
        src: Bitmap,
        content: String,
        textSize: Int,
        @ColorInt color: Int,
        x: Float,
        y: Float
    ): Bitmap? {
        return addTextWatermark(src, content, textSize.toFloat(), color, x, y, false)
    }

    /**
     * 添加文字水印
     *
     * @param src      The source of bitmap.
     * @param content  The content of text.
     * @param textSize The size of text.
     * @param color    The color of text.
     * @param x        The x coordinate of the first pixel.
     * @param y        The y coordinate of the first pixel.
     * @param recycle  True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with text watermarking
     */
    fun addTextWatermark(
        src: Bitmap,
        content: String?,
        textSize: Float,
        @ColorInt color: Int,
        x: Float,
        y: Float,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src) || content == null) return null
        val ret = src.copy(src.config, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas(ret)
        paint.color = color
        paint.textSize = textSize
        val bounds = Rect()
        paint.getTextBounds(content, 0, content.length, bounds)
        canvas.drawText(content, x, y + textSize, paint)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 添加图片水印
     *
     * @param src       The source of bitmap.
     * @param watermark The image watermarking.
     * @param x         The x coordinate of the first pixel.
     * @param y         The y coordinate of the first pixel.
     * @param alpha     The alpha of watermark.
     * @return the bitmap with image watermarking
     */
    fun addImageWatermark(
        src: Bitmap,
        watermark: Bitmap,
        x: Int, y: Int,
        alpha: Int
    ): Bitmap? {
        return addImageWatermark(src, watermark, x, y, alpha, false)
    }

    /**
     * 添加图片水印
     *
     * @param src       The source of bitmap.
     * @param watermark The image watermarking.
     * @param x         The x coordinate of the first pixel.
     * @param y         The y coordinate of the first pixel.
     * @param alpha     The alpha of watermark.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with image watermarking
     */
    fun addImageWatermark(
        src: Bitmap,
        watermark: Bitmap,
        x: Int,
        y: Int,
        alpha: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = src.copy(src.config, true)
        if (!isEmptyBitmap(watermark)) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas(ret)
            paint.alpha = alpha
            canvas.drawBitmap(watermark, x.toFloat(), y.toFloat(), paint)
        }
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 转为 alpha 位图
     *
     * @param src The source of bitmap.
     * @return the alpha bitmap
     */
    fun toAlpha(src: Bitmap): Bitmap? {
        return toAlpha(src, false)
    }

    /**
     * 转为 alpha 位图
     *
     * @param src     The source of bitmap.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the alpha bitmap
     */
    fun toAlpha(src: Bitmap, recycle: Boolean?): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = src.extractAlpha()
        if (recycle!! && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 转为灰度图片
     *
     * @param src The source of bitmap.
     * @return the gray bitmap
     */
    fun toGray(src: Bitmap): Bitmap? {
        return toGray(src, false)
    }

    /**
     * 转为灰度图片
     *
     * @param src     The source of bitmap.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the gray bitmap
     */
    fun toGray(src: Bitmap, recycle: Boolean): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val ret = Bitmap.createBitmap(src.width, src.height, src.config)
        val canvas = Canvas(ret)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(src, 0f, 0f, paint)
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * 快速模糊
     *
     * zoom out, blur, zoom in
     *
     * @param src    The source of bitmap.
     * @param scale  The scale(0...1).
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    fun fastBlur(
        src: Bitmap,
        @FloatRange(from = 0.0, to = 1.0, fromInclusive = false)
        scale: Float,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
        radius: Float
    ): Bitmap? {
        return fastBlur(src, scale, radius, false, false)
    }

    /**
     * 快速模糊
     *
     * zoom out, blur, zoom in
     *
     * @param src    The source of bitmap.
     * @param scale  The scale(0...1).
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    fun fastBlur(
        src: Bitmap,
        @FloatRange(from = 0.0, to = 1.0, fromInclusive = false)
        scale: Float,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
        radius: Float,
        recycle: Boolean
    ): Bitmap? {
        return fastBlur(src, scale, radius, recycle, false)
    }

    /**
     * 快速模糊
     *
     * zoom out, blur, zoom in
     *
     * @param src           The source of bitmap.
     * @param scale         The scale(0...1).
     * @param radius        The radius(0...25).
     * @param recycle       True to recycle the source of bitmap, false otherwise.
     * @param isReturnScale True to return the scale blur bitmap, false otherwise.
     * @return the blur bitmap
     */
    fun fastBlur(
        src: Bitmap,
        @FloatRange(from = 0.0, to = 1.0, fromInclusive = false)
        scale: Float,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
        radius: Float,
        recycle: Boolean,
        isReturnScale: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val width = src.width
        val height = src.height
        val matrix = Matrix()
        matrix.setScale(scale, scale)
        var scaleBitmap = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        val canvas = Canvas()
        val filter = PorterDuffColorFilter(
            Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP
        )
        paint.colorFilter = filter
        canvas.scale(scale, scale)
        canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            scaleBitmap = renderScriptBlur(scaleBitmap, radius, recycle)
        } else {
            scaleBitmap = stackBlur(scaleBitmap, radius.toInt(), recycle)
        }
        if (scale == 1f || isReturnScale) {
            if (recycle && !src.isRecycled && scaleBitmap != src) src.recycle()
            return scaleBitmap
        }
        val ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true)
        if (!scaleBitmap.isRecycled) scaleBitmap.recycle()
        if (recycle && !src.isRecycled && ret != src) src.recycle()
        return ret
    }

    /**
     * renderScript 模糊图片
     *
     * @param src    The source of bitmap.
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun renderScriptBlur(
        src: Bitmap,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
        radius: Float
    ): Bitmap {
        return renderScriptBlur(src, radius, false)
    }

    /**
     * renderScript 模糊图片
     *
     * @param src     The source of bitmap.
     * @param radius  The radius(0...25).
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the blur bitmap
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun renderScriptBlur(
        src: Bitmap,
        @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
        radius: Float,
        recycle: Boolean
    ): Bitmap {
        var rs: RenderScript? = null
        val ret = if (recycle) src else src.copy(src.config, true)
        try {
            rs = RenderScript.create(AppManager.getInstance().getApp())
            rs!!.messageHandler = RenderScript.RSMessageHandler()
            val input = Allocation.createFromBitmap(
                rs,
                ret,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(rs, input.type)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blurScript.setInput(input)
            blurScript.setRadius(radius)
            blurScript.forEach(output)
            output.copyTo(ret)
        } finally {
            rs?.destroy()
        }
        return ret
    }

    /**
     * stack 模糊图片
     *
     * @param src    The source of bitmap.
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    fun stackBlur(src: Bitmap, radius: Int): Bitmap {
        return stackBlur(src, radius, false)
    }

    /**
     * stack 模糊图片
     *
     * @param src     The source of bitmap.
     * @param radius  The radius(0...25).
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the blur bitmap
     */
    fun stackBlur(src: Bitmap, radius: Int, recycle: Boolean): Bitmap {
        var radius = radius
        val ret = if (recycle) src else src.copy(src.config, true)
        if (radius < 1) {
            radius = 1
        }
        val w = ret.width
        val h = ret.height

        val pix = IntArray(w * h)
        ret.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] =
                    -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h)
        return ret
    }

    /**
     * 保存图片
     *
     * @param src      The source of bitmap.
     * @param filePath The path of file.
     * @param format   The format of the image.
     * @return `true`: success<br></br>`false`: fail
     */
    fun save(
        src: Bitmap,
        filePath: String,
        format: Bitmap.CompressFormat
    ): Boolean {
        return save(src, getFileByPath(filePath), format, false)
    }

    /**
     * 保存图片
     *
     * @param src    The source of bitmap.
     * @param file   The file.
     * @param format The format of the image.
     * @return `true`: success<br></br>`false`: fail
     */
    fun save(src: Bitmap, file: File, format: Bitmap.CompressFormat): Boolean {
        return save(src, file, format, false)
    }

    /**
     * 保存图片
     *
     * @param src      The source of bitmap.
     * @param filePath The path of file.
     * @param format   The format of the image.
     * @param recycle  True to recycle the source of bitmap, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    fun save(
        src: Bitmap,
        filePath: String,
        format: Bitmap.CompressFormat,
        recycle: Boolean
    ): Boolean {
        return save(src, getFileByPath(filePath), format, recycle)
    }

    /**
     * 保存图片
     *
     * @param src     The source of bitmap.
     * @param file    The file.
     * @param format  The format of the image.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    fun save(
        src: Bitmap,
        file: File?,
        format: Bitmap.CompressFormat,
        recycle: Boolean
    ): Boolean {
        if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) return false
        var os: OutputStream? = null
        var ret = false
        try {
            os = BufferedOutputStream(FileOutputStream(file!!))
            ret = src.compress(format, 100, os)
            if (recycle && !src.isRecycled) src.recycle()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return ret
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isImage(file: File?): Boolean {
        return file != null && isImage(file.path)
    }

    /**
     * 根据文件名判断文件是否为图片
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isImage(filePath: String): Boolean {
        val path = filePath.toUpperCase()
        return (path.endsWith(".PNG") || path.endsWith(".JPG")
                || path.endsWith(".JPEG") || path.endsWith(".BMP")
                || path.endsWith(".GIF") || path.endsWith(".WEBP"))
    }

    /**
     * 获取图片类型
     *
     * @param filePath The path of file.
     * @return the type of image
     */
    fun getImageType(filePath: String): String {
        return getImageType(getFileByPath(filePath))
    }

    /**
     * 获取图片类型
     *
     * @param file The file.
     * @return the type of image
     */
    fun getImageType(file: File?): String {
        if (file == null) return ""
        var `is`: InputStream? = null
        try {
            `is` = FileInputStream(file)
            val type = getImageType(`is`)
            if (type != null) {
                return type
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return getFileExtension(file.absolutePath)!!.toUpperCase()
    }

    private fun getFileExtension(filePath: String): String? {
        if (isSpace(filePath)) return filePath
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    private fun getImageType(`is`: InputStream?): String? {
        if (`is` == null) return null
        try {
            val bytes = ByteArray(8)
            return if (`is`.read(bytes, 0, 8) != -1) getImageType(bytes) else null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    private fun getImageType(bytes: ByteArray): String? {
        if (isJPEG(bytes)) return "JPEG"
        if (isGIF(bytes)) return "GIF"
        if (isPNG(bytes)) return "PNG"
        return if (isBMP(bytes)) "BMP" else null
    }

    private fun isJPEG(b: ByteArray): Boolean {
        return (b.size >= 2
                && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte())
    }

    private fun isGIF(b: ByteArray): Boolean {
        return (b.size >= 6
                && b[0] == 'G'.toByte() && b[1] == 'I'.toByte()
                && b[2] == 'F'.toByte() && b[3] == '8'.toByte()
                && (b[4] == '7'.toByte() || b[4] == '9'.toByte()) && b[5] == 'a'.toByte())
    }

    private fun isPNG(b: ByteArray): Boolean {
        return b.size >= 8 && (b[0] == 137.toByte() && b[1] == 80.toByte()
                && b[2] == 78.toByte() && b[3] == 71.toByte()
                && b[4] == 13.toByte() && b[5] == 10.toByte()
                && b[6] == 26.toByte() && b[7] == 10.toByte())
    }

    private fun isBMP(b: ByteArray): Boolean {
        return (b.size >= 2
                && b[0].toInt() == 0x42 && b[1].toInt() == 0x4d)
    }

    private fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }

    ///////////////////////////////////////////////////////////////////////////
    // about compress
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 按缩放压缩
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @return the compressed bitmap
     */
    fun compressByScale(
        src: Bitmap,
        newWidth: Int,
        newHeight: Int
    ): Bitmap? {
        return scale(src, newWidth, newHeight, false)
    }

    /**
     * 按缩放压缩
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    fun compressByScale(
        src: Bitmap,
        newWidth: Int,
        newHeight: Int,
        recycle: Boolean
    ): Bitmap? {
        return scale(src, newWidth, newHeight, recycle)
    }

    /**
     * 按缩放压缩
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @return the compressed bitmap
     */
    fun compressByScale(
        src: Bitmap,
        scaleWidth: Float,
        scaleHeight: Float
    ): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, false)
    }

    /**
     * 按缩放压缩
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return he compressed bitmap
     */
    fun compressByScale(
        src: Bitmap,
        scaleWidth: Float,
        scaleHeight: Float,
        recycle: Boolean
    ): Bitmap? {
        return scale(src, scaleWidth, scaleHeight, recycle)
    }

    /**
     * 按质量压缩
     *
     * @param src     The source of bitmap.
     * @param quality The quality.
     * @return the compressed bitmap
     */
    fun compressByQuality(
        src: Bitmap,
        @IntRange(from = 0, to = 100) quality: Int
    ): Bitmap? {
        return compressByQuality(src, quality, false)
    }

    /**
     * 按质量压缩
     *
     * @param src     The source of bitmap.
     * @param quality The quality.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    fun compressByQuality(
        src: Bitmap,
        @IntRange(from = 0, to = 100) quality: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val baos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 按质量压缩
     *
     * @param src         The source of bitmap.
     * @param maxByteSize The maximum size of byte.
     * @return the compressed bitmap
     */
    fun compressByQuality(src: Bitmap, maxByteSize: Long): Bitmap? {
        return compressByQuality(src, maxByteSize, false)
    }

    /**
     * 按质量压缩
     *
     * @param src         The source of bitmap.
     * @param maxByteSize The maximum size of byte.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    fun compressByQuality(
        src: Bitmap,
        maxByteSize: Long,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null
        val baos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bytes: ByteArray
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray()
        } else {
            baos.reset()
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos)
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray()
            } else {
                // find the best quality using binary search
                var st = 0
                var end = 100
                var mid = 0
                while (st < end) {
                    mid = (st + end) / 2
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos)
                    val len = baos.size()
                    if (len.toLong() == maxByteSize) {
                        break
                    } else if (len > maxByteSize) {
                        end = mid - 1
                    } else {
                        st = mid + 1
                    }
                }
                if (end == mid - 1) {
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos)
                }
                bytes = baos.toByteArray()
            }
        }
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 合并Bitmap
     * @param bgd 背景Bitmap
     * @param fg 前景Bitmap
     * @return 合成后的Bitmap
     */
    fun combineImages(bgd: Bitmap, fg: Bitmap): Bitmap {
        val bmp: Bitmap

        val width = if (bgd.width > fg.width)
            bgd.width
        else
            fg
                .width
        val height = if (bgd.height > fg.height)
            bgd.height
        else
            fg
                .height

        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

        val canvas = Canvas(bmp)
        canvas.drawBitmap(bgd, 0f, 0f, null)
        canvas.drawBitmap(fg, 0f, 0f, paint)

        return bmp
    }

    /**
     * 合并
     * @param bgd 后景Bitmap
     * @param fg 前景Bitmap
     * @return 合成后Bitmap
     */
    fun combineImagesToSameSize(bgd: Bitmap, fg: Bitmap): Bitmap {
        var bgd = bgd
        var fg = fg
        val bmp: Bitmap

        val width = if (bgd.width < fg.width)
            bgd.width
        else
            fg
                .width
        val height = if (bgd.height < fg.height)
            bgd.height
        else
            fg
                .height

        if (fg.width != width && fg.height != height) {
            fg = zoom(fg, width, height)
        }
        if (bgd.width != width && bgd.height != height) {
            bgd = zoom(bgd, width, height)
        }

        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

        val canvas = Canvas(bmp)
        canvas.drawBitmap(bgd, 0f, 0f, null)
        canvas.drawBitmap(fg, 0f, 0f, paint)

        return bmp
    }

    /**
     * 放大缩小图片
     *
     * @param bitmap 源Bitmap
     * @param w 宽
     * @param h 高
     * @return 目标Bitmap
     */
    fun zoom(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidht = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        matrix.postScale(scaleWidht, scaleHeight)
        return Bitmap.createBitmap(
            bitmap, 0, 0, width, height,
            matrix, true
        )
    }

    /**
     * 按采样大小压缩
     *
     * @param src        The source of bitmap.
     * @param sampleSize The sample size.
     * @return the compressed bitmap
     */

    fun compressBySampleSize(src: Bitmap, sampleSize: Int): Bitmap? {
        return compressBySampleSize(src, sampleSize, false)
    }

    /**
     * 按采样大小压缩
     *
     * @param src        The source of bitmap.
     * @param sampleSize The sample size.
     * @param recycle    True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    fun compressBySampleSize(
        src: Bitmap,
        sampleSize: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        val baos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    /**
     * 按采样大小压缩
     *
     * @param src       The source of bitmap.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the compressed bitmap
     */
    fun compressBySampleSize(
        src: Bitmap,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        return compressBySampleSize(src, maxWidth, maxHeight, false)
    }

    /**
     * 按采样大小压缩
     *
     * @param src       The source of bitmap.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    fun compressBySampleSize(
        src: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src)) return null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val baos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    /**
     * 获取图片尺寸
     *
     * @param filePath The path of file.
     * @return the size of bitmap
     */
    fun getSize(filePath: String): IntArray {
        return getSize(getFileByPath(filePath))
    }

    /**
     * 获取图片尺寸
     *
     * @param file The file.
     * @return the size of bitmap
     */
    fun getSize(file: File?): IntArray {
        if (file == null) return intArrayOf(0, 0)
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, opts)
        return intArrayOf(opts.outWidth, opts.outHeight)
    }

    /**
     * Return the sample size.
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        var height = options.outHeight
        var width = options.outWidth
        var inSampleSize = 1
        width = width shr 1
        height = height shr 1
        while (width >= maxWidth && height >= maxHeight) {
            width = width shr 1
            height = height shr 1
            inSampleSize = inSampleSize shl 1
        }
        return inSampleSize
    }

    ///////////////////////////////////////////////////////////////////////////
    // other utils methods
    ///////////////////////////////////////////////////////////////////////////

    private fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    private fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists() && !file.delete()) return false
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    private fun input2Byte(inputStream: InputStream?): ByteArray? {
        if (inputStream == null) return null
        try {
            val os = ByteArrayOutputStream()
            val b = ByteArray(1024)
            if (inputStream.read(b, 0, 1024) != -1) {
                os.write(b, 0, inputStream.read(b, 0, 1024))
            }
            return os.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}