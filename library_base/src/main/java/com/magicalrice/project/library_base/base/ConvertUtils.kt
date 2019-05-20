package com.magicalrice.project.library_base.base

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.magicalrice.project.library_base.base.constant.*
import java.io.*
import java.nio.charset.Charset

/**
 * @package com.magicalrice.project.library_base.base
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description 转换相关
 *
 * bytes2Bits, bits2Bytes                  : bytes 与 bits 互转
 * bytes2Chars, chars2Bytes                : bytes 与 chars 互转
 * bytes2HexString, hexString2Bytes        : bytes 与 hexString 互转
 * memorySize2Byte, byte2MemorySize        : 以 unit 为单位的内存大小与字节数互转
 * byte2FitMemorySize                      : 字节数转合适内存大小
 * timeSpan2Millis, millis2TimeSpan        : 以 unit 为单位的时间长度与毫秒时间戳互转
 * millis2FitTimeSpan                      : 毫秒时间戳转合适时间长度
 * input2OutputStream, output2InputStream  : inputStream 与 outputStream 互转
 * inputStream2Bytes, bytes2InputStream    : inputStream 与 bytes 互转
 * outputStream2Bytes, bytes2OutputStream  : outputStream 与 bytes 互转
 * inputStream2String, string2InputStream  : inputStream 与 string 按编码互转
 * outputStream2String, string2OutputStream: outputStream 与 string 按编码互转
 * bitmap2Bytes, bytes2Bitmap              : bitmap 与 bytes 互转
 * drawable2Bitmap, bitmap2Drawable        : drawable 与 bitmap 互转
 * drawable2Bytes, bytes2Drawable          : drawable 与 bytes 互转
 * view2Bitmap                             : view 转 Bitmap
 * dp2px, px2dp                            : dp 与 px 互转
 * sp2px, px2sp                            : sp 与 px 互转
 */

object ConvertUtils {
    private val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    /**
     * Bytes 转 bits.
     *
     * @param bytes The bytes.
     * @return bits
     */
    fun bytes2Bits(bytes: ByteArray?): String {
        if (bytes == null || bytes.isEmpty()) return ""
        val sb = StringBuilder()
        for (aByte in bytes) {
            for (j in 7 downTo 0) {
                sb.append(if (aByte.toInt() shr j and 0x01 == 0) '0' else '1')
            }
        }
        return sb.toString()
    }

    /**
     * Bits 转 bytes.
     *
     * @param bits The bits.
     * @return bytes
     */
    fun bits2Bytes(bits: String): ByteArray {
        var bits = bits
        val lenMod = bits.length % 8
        var byteLen = bits.length / 8
        // add "0" until length to 8 times
        if (lenMod != 0) {
            for (i in lenMod..7) {
                bits = "0$bits"
            }
            byteLen++
        }
        val bytes = ByteArray(byteLen)
        for (i in 0 until byteLen) {
            for (j in 0..7) {
                bytes[i] = (bytes[i].toInt() shl 1).toByte()
                bytes[i] = (bytes[i].toInt() or (bits[i * 8 + j] - '0').toByte().toInt()).toByte()
            }
        }
        return bytes
    }

    /**
     * Bytes 转 chars.
     *
     * @param bytes The bytes.
     * @return chars
     */
    fun bytes2Chars(bytes: ByteArray?): CharArray? {
        if (bytes == null) return null
        val len = bytes.size
        if (len <= 0) return null
        val chars = CharArray(len)
        for (i in 0 until len) {
            chars[i] = (bytes[i].toInt() and 0xff).toChar()
        }
        return chars
    }

    /**
     * Chars 转 bytes.
     *
     * @param chars The chars.
     * @return bytes
     */
    fun chars2Bytes(chars: CharArray?): ByteArray? {
        if (chars == null || chars.size <= 0) return null
        val len = chars.size
        val bytes = ByteArray(len)
        for (i in 0 until len) {
            bytes[i] = chars[i].toByte()
        }
        return bytes
    }

    /**
     * Bytes 转 hex string.
     *
     * e.g. bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns "00A8"
     *
     * @param bytes The bytes.
     * @return hex string
     */
    fun bytes2HexString(bytes: ByteArray?): String {
        if (bytes == null) return ""
        val len = bytes.size
        if (len <= 0) return ""
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[bytes[i].toInt() shr 4 and 0x0f]
            ret[j++] = hexDigits[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }

    /**
     * Hex string 转 bytes.
     *
     * e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString The hex string.
     * @return the bytes
     */
    fun hexString2Bytes(hexString: String): ByteArray? {
        var hexString = hexString
        if (isSpace(hexString)) return null
        var len = hexString.length
        if (len % 2 != 0) {
            hexString = "0$hexString"
            len = len + 1
        }
        val hexBytes = hexString.toUpperCase().toCharArray()
        val ret = ByteArray(len shr 1)
        var i = 0
        while (i < len) {
            ret[i shr 1] = (hex2Int(hexBytes[i]) shl 4 or hex2Int(hexBytes[i + 1])).toByte()
            i += 2
        }
        return ret
    }

    private fun hex2Int(hexChar: Char): Int {
        return if (hexChar >= '0' && hexChar <= '9') {
            hexChar - '0'
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            hexChar - 'A' + 10
        } else {
            throw IllegalArgumentException()
        }
    }

    /**
     * 以 unit 为单位的内存大小与字节数互转
     *
     * @param memorySize Size of memory.
     * @param unit       The unit of memory size.
     *
     *  * [MemoryConstants.BYTE]
     *  * [MemoryConstants.KB]
     *  * [MemoryConstants.MB]
     *  * [MemoryConstants.GB]
     *
     * @return size of byte
     */
    fun memorySize2Byte(
        memorySize: Long,
        @MemoryConstants unit: Int
    ): Long {
        return if (memorySize < 0) -1 else memorySize * unit
    }

    /**
     * 以 unit 为单位的内存大小与字节数互转
     *
     * @param byteSize Size of byte.
     * @param unit     The unit of memory size.
     *
     *  * [MemoryConstants.BYTE]
     *  * [MemoryConstants.KB]
     *  * [MemoryConstants.MB]
     *  * [MemoryConstants.GB]
     *
     * @return size of memory in unit
     */
    fun byte2MemorySize(
        byteSize: Long,
        @MemoryConstants
        unit: Int
    ): Double {
        return if (byteSize < 0) -1.0 else byteSize.toDouble() / unit
    }

    /**
     * 字节数转合适内存大小
     *
     * to three decimal places
     *
     * @param byteSize Size of byte.
     * @return fit size of memory
     */
    @SuppressLint("DefaultLocale")
    fun byte2FitMemorySize(byteSize: Long): String {
        return if (byteSize < 0) {
            "shouldn't be less than zero!"
        } else if (byteSize < KB) {
            String.format("%.3fB", byteSize.toDouble())
        } else if (byteSize < MB) {
            String.format("%.3fKB", byteSize.toDouble() / KB)
        } else if (byteSize < GB) {
            String.format("%.3fMB", byteSize.toDouble() / MB)
        } else {
            String.format("%.3fGB", byteSize.toDouble() / GB)
        }
    }

    /**
     * 以 unit 为单位的时间长度与毫秒时间戳互转
     *
     * @param timeSpan The time span.
     * @param unit     The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return milliseconds
     */
    fun timeSpan2Millis(timeSpan: Long, @TimeConstants unit: Int): Long {
        return timeSpan * unit
    }

    /**
     * 以 unit 为单位的时间长度与毫秒时间戳互转
     *
     * @param millis The milliseconds.
     * @param unit   The unit of time span.
     *
     *  * [TimeConstants.MSEC]
     *  * [TimeConstants.SEC]
     *  * [TimeConstants.MIN]
     *  * [TimeConstants.HOUR]
     *  * [TimeConstants.DAY]
     *
     * @return time span in unit
     */
    fun millis2TimeSpan(millis: Long, @TimeConstants unit: Int): Long {
        return millis / unit
    }

    /**
     * 毫秒时间戳转合适时间长度
     *
     * @param millis    The milliseconds.
     *
     * millis &lt;= 0, return null
     * @param precision The precision of time span.
     *
     *  * precision = 0, return null
     *  * precision = 1, return 天
     *  * precision = 2, return 天, 小时
     *  * precision = 3, return 天, 小时, 分钟
     *  * precision = 4, return 天, 小时, 分钟, 秒
     *  * precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒
     *
     * @return fit time span
     */
    @SuppressLint("DefaultLocale")
    fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
        var millis = millis
        var precision = precision
        if (millis <= 0 || precision <= 0) return null
        val sb = StringBuilder()
        val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
        val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
        precision = Math.min(precision, 5)
        for (i in 0 until precision) {
            if (millis >= unitLen[i]) {
                val mode = millis / unitLen[i]
                millis -= mode * unitLen[i]
                sb.append(mode).append(units[i])
            }
        }
        return sb.toString()
    }

    /**
     * Input stream 转 output stream.
     *
     * @param is The input stream.
     * @return output stream
     */
    fun input2OutputStream(inputStream: InputStream?): ByteArrayOutputStream? {
        if (inputStream == null) return null
        try {
            val os = ByteArrayOutputStream()
            val b = ByteArray(KB)
            var len: Int = inputStream.read(b, 0, KB)
            while (len != -1) {
                os.write(b, 0, len)
                len = inputStream.read(b, 0, KB)
            }
            return os
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

    /**
     * Output stream 转 input stream.
     *
     * @param out The output stream.
     * @return input stream
     */
    fun output2InputStream(out: OutputStream?): ByteArrayInputStream? {
        return if (out == null) null else ByteArrayInputStream((out as ByteArrayOutputStream).toByteArray())
    }

    /**
     * Input stream 转 bytes.
     *
     * @param is The input stream.
     * @return bytes
     */
    fun inputStream2Bytes(inputStream: InputStream?): ByteArray? {
        return if (inputStream == null) null else input2OutputStream(inputStream)!!.toByteArray()
    }

    /**
     * Bytes 转 input stream.
     *
     * @param bytes The bytes.
     * @return input stream
     */
    fun bytes2InputStream(bytes: ByteArray?): InputStream? {
        return if (bytes == null || bytes.isEmpty()) null else ByteArrayInputStream(bytes)
    }

    /**
     * Output stream 转 bytes.
     *
     * @param out The output stream.
     * @return bytes
     */
    fun outputStream2Bytes(out: OutputStream?): ByteArray? {
        return if (out == null) null else (out as ByteArrayOutputStream).toByteArray()
    }

    /**
     * Bytes 转 output stream.
     *
     * @param bytes The bytes.
     * @return output stream
     */
    fun bytes2OutputStream(bytes: ByteArray?): OutputStream? {
        if (bytes == null || bytes.size <= 0) return null
        var os: ByteArrayOutputStream? = null
        try {
            os = ByteArrayOutputStream()
            os.write(bytes)
            return os
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    /**
     * Input stream 转 string.
     *
     * @param is          The input stream.
     * @param charsetName The name of charset.
     * @return string
     */
    fun inputStream2String(inputStream: InputStream?, charsetName: String): String {
        if (inputStream == null || isSpace(charsetName)) return ""
        return try {
            val byteArray = inputStream2Bytes(inputStream)
            if (byteArray == null) {
                ""
            } else {
                String(byteArray, Charset.forName(charsetName))
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }

    }

    /**
     * String 转 input stream.
     *
     * @param string      The string.
     * @param charsetName The name of charset.
     * @return input stream
     */
    fun string2InputStream(string: String?, charsetName: String): InputStream? {
        if (string == null || isSpace(charsetName)) return null
        try {
            return ByteArrayInputStream(string.toByteArray(charset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * Output stream 转 string.
     *
     * @param out         The output stream.
     * @param charsetName The name of charset.
     * @return string
     */
    fun outputStream2String(out: OutputStream?, charsetName: String): String {
        if (out == null || isSpace(charsetName)) return ""
        try {
            val byteArray = outputStream2Bytes(out)
            if (byteArray == null) {
                return ""
            } else {
                return String(byteArray, Charset.forName(charsetName))
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * String 转 output stream.
     *
     * @param string      The string.
     * @param charsetName The name of charset.
     * @return output stream
     */
    fun string2OutputStream(string: String?, charsetName: String): OutputStream? {
        if (string == null || isSpace(charsetName)) return null
        try {
            return bytes2OutputStream(string.toByteArray(charset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }

    }

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
    fun drawable2Bytes(
        drawable: Drawable?,
        format: Bitmap.CompressFormat
    ): ByteArray? {
        return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format)
    }

    /**
     * Bytes 转 drawable.
     *
     * @param bytes The bytes.
     * @return drawable
     */
    fun bytes2Drawable(bytes: ByteArray?): Drawable? {
        return if (bytes == null) null else bitmap2Drawable(bytes2Bitmap(bytes))
    }

    /**
     * View 转 bitmap.
     *
     * @param view The view.
     * @return bitmap
     */
    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val ret = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
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
     * Value of dp 转 value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    fun dp2px(dpValue: Float): Int {
        val scale = AppManager.getInstance().getApp().getResources().getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * Value of px 转 value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    fun px2dp(pxValue: Float): Int {
        val scale = AppManager.getInstance().getApp().getResources().getDisplayMetrics().density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * Value of sp 转 value of px.
     *
     * @param spValue The value of sp.
     * @return value of px
     */
    fun sp2px(spValue: Float): Int {
        val fontScale =
            AppManager.getInstance().getApp().getResources().getDisplayMetrics().scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * Value of px 转 value of sp.
     *
     * @param pxValue The value of px.
     * @return value of sp
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale =
            AppManager.getInstance().getApp().getResources().getDisplayMetrics().scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    ///////////////////////////////////////////////////////////////////////////
    // other utils methods
    ///////////////////////////////////////////////////////////////////////////

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
}