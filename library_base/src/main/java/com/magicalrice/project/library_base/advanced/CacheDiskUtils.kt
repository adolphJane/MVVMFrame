package com.magicalrice.project.library_base.advanced

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.magicalrice.project.library_base.base.AppManager
import com.magicalrice.project.library_base.base.constant.CacheConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * 磁盘缓存相关
 *
 * getInstance             : 获取缓存实例
 * Instance.put            : 缓存中写入数据
 * Instance.getBytes       : 缓存中读取字节数组
 * Instance.getString      : 缓存中读取 String
 * Instance.getJSONObject  : 缓存中读取 JSONObject
 * Instance.getJSONArray   : 缓存中读取 JSONArray
 * Instance.getBitmap      : 缓存中读取 Bitmap
 * Instance.getDrawable    : 缓存中读取 Drawable
 * Instance.getParcelable  : 缓存中读取 Parcelable
 * Instance.getSerializable: 缓存中读取 Serializable
 * Instance.getCacheSize   : 获取缓存大小
 * Instance.getCacheCount  : 获取缓存个数
 * Instance.remove         : 根据键值移除缓存
 * Instance.clear          : 清除所有缓存
 */
class CacheDiskUtils private constructor(
    private val mCacheKey: String,
    private val mCacheDir: File,
    private val mMaxSize: Long,
    private val mMaxCount: Int
) : CacheConstants {
    private var mDiskCacheManager: DiskCacheManager? = null

    private val diskCacheManager: DiskCacheManager?
        get() {
            if (mCacheDir.exists()) {
                if (mDiskCacheManager == null) {
                    mDiskCacheManager = DiskCacheManager(mCacheDir, mMaxSize, mMaxCount)
                }
            } else {
                if (mCacheDir.mkdirs()) {
                    mDiskCacheManager = DiskCacheManager(mCacheDir, mMaxSize, mMaxCount)
                } else {
                    Log.e("CacheDiskUtils", "can't make dirs in " + mCacheDir.absolutePath)
                }
            }
            return mDiskCacheManager
        }

    /**
     * 获取缓存大小
     *
     * @return the size of cache, in bytes
     */
    val cacheSize: Long
        get() {
            val diskCacheManager = diskCacheManager ?: return 0
            return diskCacheManager.getCacheSize()
        }

    /**
     * 获取缓存个数
     *
     * @return the count of cache
     */
    val cacheCount: Int
        get() {
            val diskCacheManager = diskCacheManager ?: return 0
            return diskCacheManager.getCacheCount()
        }

    override fun toString(): String {
        return mCacheKey + "@" + Integer.toHexString(hashCode())
    }

    /**
     * 缓存中写入字节数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: ByteArray?, saveTime: Int = -1) {
        var value: ByteArray? = value ?: return
        val diskCacheManager = diskCacheManager ?: return
        if (saveTime >= 0) value = DiskCacheHelper.newByteArrayWithTime(saveTime, value!!)
        val file = diskCacheManager.getFileBeforePut(key)
        writeFileFromBytes(file, value)
        diskCacheManager.updateModify(file)
        diskCacheManager.put(file)
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bytes if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getBytes(key: String, defaultValue: ByteArray? = null): ByteArray? {
        val diskCacheManager = diskCacheManager ?: return defaultValue
        val file = diskCacheManager.getFileIfExists(key) ?: return defaultValue
        val data = readFile2Bytes(file)
        if (DiskCacheHelper.isDue(data!!)) {
            diskCacheManager.removeByKey(key)
            return defaultValue
        }
        diskCacheManager.updateModify(file)
        return DiskCacheHelper.getDataWithoutDueTime(data)
    }

    /**
     * 缓存中写入string数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: String, saveTime: Int = -1) {
        put(key, string2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取 String
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the string value if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getString(key: String, defaultValue: String? = null): String? {
        val bytes = getBytes(key) ?: return defaultValue
        return bytes2String(bytes)
    }

    /**
     * 缓存中写入json数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(
        key: String,
        value: JSONObject,
        saveTime: Int = -1
    ) {
        put(key, jsonObject2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取 JSONObject
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getJSONObject(key: String, defaultValue: JSONObject? = null): JSONObject? {
        val bytes = getBytes(key) ?: return defaultValue
        return bytes2JSONObject(bytes)
    }

    /**
     * 缓存中写入jsonarray数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: JSONArray, saveTime: Int = -1) {
        put(key, jsonArray2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取 JSONArray
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getJSONArray(key: String, defaultValue: JSONArray? = null): JSONArray? {
        val bytes = getBytes(key) ?: return defaultValue
        return bytes2JSONArray(bytes)
    }

    /**
     * 缓存中写入bitmap数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Bitmap, saveTime: Int = -1) {
        put(key, bitmap2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取 Bitmap
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getBitmap(key: String, defaultValue: Bitmap? = null): Bitmap? {
        val bytes = getBytes(key) ?: return defaultValue
        return bytes2Bitmap(bytes)
    }

    /**
     * 缓存中写入drawable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Drawable, saveTime: Int = -1) {
        put(key, drawable2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取 Drawable
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the drawable if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getDrawable(key: String, defaultValue: Drawable? = null): Drawable? {
        val bytes = getBytes(key) ?: return defaultValue
        return bytes2Drawable(bytes)
    }

    /**
     * 缓存中写入parcelable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Parcelable, saveTime: Int = -1) {
        put(key, parcelable2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取 Parcelable
     *
     * @param key     The key of cache.
     * @param creator The creator.
     * @param <T>     The value type.
     * @return the parcelable if cache exists or null otherwise
    </T> */
    fun <T> getParcelable(
        key: String,
        creator: Parcelable.Creator<T>
    ): T? {
        return getParcelable(key, creator, null)
    }

    /**
     * 缓存中读取 Parcelable
     *
     * @param key          The key of cache.
     * @param creator      The creator.
     * @param defaultValue The default value if the cache doesn't exist.
     * @param <T>          The value type.
     * @return the parcelable if cache exists or defaultValue otherwise
    </T> */
    fun <T> getParcelable(
        key: String,
        creator: Parcelable.Creator<T>,
        defaultValue: T?
    ): T? {
        val bytes = getBytes(key) ?: return defaultValue
        return bytes2Parcelable(bytes, creator)
    }

    /**
     * 缓存中写入serializable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    @JvmOverloads
    fun put(key: String, value: Serializable, saveTime: Int = -1) {
        put(key, serializable2Bytes(value), saveTime)
    }

    /**
     * 缓存中读取 Serializable
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    @JvmOverloads
    fun getSerializable(key: String, defaultValue: Any? = null): Any? {
        val bytes = getBytes(key) ?: return defaultValue
        return bytes2Object(getBytes(key))
    }

    /**
     * 根据键值移除缓存
     *
     * @param key The key of cache.
     * @return `true`: success<br></br>`false`: fail
     */
    fun remove(key: String): Boolean {
        val diskCacheManager = diskCacheManager ?: return true
        return diskCacheManager.removeByKey(key)
    }

    /**
     * 清除所有缓存
     *
     * @return `true`: success<br></br>`false`: fail
     */
    fun clear(): Boolean {
        val diskCacheManager = diskCacheManager ?: return true
        return diskCacheManager.clear()
    }

    private class DiskCacheManager constructor(
        private val cacheDir: File,
        private val sizeLimit: Long,
        private val countLimit: Int
    ) {
        private val cacheSize: AtomicLong
        private val cacheCount: AtomicInteger
        private val lastUsageDates = Collections.synchronizedMap(HashMap<File, Long>())
        private val mThread: Thread

        init {
            cacheSize = AtomicLong()
            cacheCount = AtomicInteger()
            mThread = Thread(Runnable {
                var size = 0
                var count = 0
                val cachedFiles = cacheDir.listFiles { dir, name -> name.startsWith(CACHE_PREFIX) }
                if (cachedFiles != null) {
                    for (cachedFile in cachedFiles) {
                        size += cachedFile.length().toInt()
                        count += 1
                        lastUsageDates[cachedFile] = cachedFile.lastModified()
                    }
                    cacheSize.getAndAdd(size.toLong())
                    cacheCount.getAndAdd(count)
                }
            })
            mThread.start()
        }

        fun getCacheSize(): Long {
            try {
                mThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return cacheSize.get()
        }

        fun getCacheCount(): Int {
            try {
                mThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return cacheCount.get()
        }

        fun getFileBeforePut(key: String): File {
            val file = File(cacheDir, CACHE_PREFIX + key.hashCode().toString())
            if (file.exists()) {
                cacheCount.addAndGet(-1)
                cacheSize.addAndGet(-file.length())
            }
            return file
        }

        fun getFileIfExists(key: String): File? {
            val file = File(cacheDir, CACHE_PREFIX + key.hashCode().toString())
            return if (!file.exists()) null else file
        }

        fun put(file: File) {
            cacheCount.addAndGet(1)
            cacheSize.addAndGet(file.length())
            while (cacheCount.get() > countLimit || cacheSize.get() > sizeLimit) {
                cacheSize.addAndGet(-removeOldest())
                cacheCount.addAndGet(-1)
            }
        }

        fun updateModify(file: File) {
            val millis = System.currentTimeMillis()
            file.setLastModified(millis)
            lastUsageDates[file] = millis
        }

        fun removeByKey(key: String): Boolean {
            val file = getFileIfExists(key) ?: return true
            if (!file.delete()) return false
            cacheSize.addAndGet(-file.length())
            cacheCount.addAndGet(-1)
            lastUsageDates.remove(file)
            return true
        }

        fun clear(): Boolean {
            val files = cacheDir.listFiles { dir, name -> name.startsWith(CACHE_PREFIX) }
            if (files == null || files.size <= 0) return true
            var flag = true
            for (file in files) {
                if (!file.delete()) {
                    flag = false
                    continue
                }
                cacheSize.addAndGet(-file.length())
                cacheCount.addAndGet(-1)
                lastUsageDates.remove(file)
            }
            if (flag) {
                lastUsageDates.clear()
                cacheSize.set(0)
                cacheCount.set(0)
            }
            return flag
        }

        /**
         * Remove the oldest files.
         *
         * @return the size of oldest files, in bytes
         */
        private fun removeOldest(): Long {
            if (lastUsageDates.isEmpty()) return 0
            var oldestUsage: Long? = java.lang.Long.MAX_VALUE
            var oldestFile: File? = null
            val entries = lastUsageDates.entries
            synchronized(lastUsageDates) {
                for ((key, lastValueUsage) in entries) {
                    if (lastValueUsage < oldestUsage ?: 0) {
                        oldestUsage = lastValueUsage
                        oldestFile = key
                    }
                }
            }
            if (oldestFile == null) return 0
            val fileSize = oldestFile!!.length()
            if (oldestFile!!.delete()) {
                lastUsageDates.remove(oldestFile)
                return fileSize
            }
            return 0
        }
    }

    private object DiskCacheHelper {

        val TIME_INFO_LEN = 14

        fun newByteArrayWithTime(second: Int, data: ByteArray): ByteArray {
            val time = createDueTime(second).toByteArray()
            val content = ByteArray(time.size + data.size)
            System.arraycopy(time, 0, content, 0, time.size)
            System.arraycopy(data, 0, content, time.size, data.size)
            return content
        }

        /**
         * Return the string of due time.
         *
         * @param seconds The seconds.
         * @return the string of due time
         */
        fun createDueTime(seconds: Int): String {
            return String.format(
                Locale.getDefault(), "_$%010d\$_",
                System.currentTimeMillis() / 1000 + seconds
            )
        }

        fun isDue(data: ByteArray): Boolean {
            val millis = getDueTime(data)
            return millis != -1L && System.currentTimeMillis() > millis
        }

        fun getDueTime(data: ByteArray): Long {
            if (hasTimeInfo(data)) {
                val millis = String(copyOfRange(data, 2, 12))
                try {
                    return java.lang.Long.parseLong(millis) * 1000
                } catch (e: NumberFormatException) {
                    return -1
                }

            }
            return -1
        }

        fun getDataWithoutDueTime(data: ByteArray): ByteArray {
            return if (hasTimeInfo(data)) {
                copyOfRange(data, TIME_INFO_LEN, data.size)
            } else data
        }

        fun copyOfRange(original: ByteArray, from: Int, to: Int): ByteArray {
            val newLength = to - from
            if (newLength < 0) throw IllegalArgumentException("$from > $to")
            val copy = ByteArray(newLength)
            System.arraycopy(original, from, copy, 0, Math.min(original.size - from, newLength))
            return copy
        }

        fun hasTimeInfo(data: ByteArray?): Boolean {
            return (data != null
                    && data.size >= TIME_INFO_LEN
                    && data[0] == '_'.toByte()
                    && data[1] == '$'.toByte()
                    && data[12] == '$'.toByte()
                    && data[13] == '_'.toByte())
        }
    }

    companion object {

        private val DEFAULT_MAX_SIZE = java.lang.Long.MAX_VALUE
        private val DEFAULT_MAX_COUNT = Integer.MAX_VALUE
        private val CACHE_PREFIX = "cdu"

        private val CACHE_MAP = ConcurrentHashMap<String, CacheDiskUtils>()

        /**
         * 获取缓存实例
         *
         * cache directory: /data/data/package/cache/cacheUtils
         *
         * cache size: unlimited
         *
         * cache count: unlimited
         *
         * @return the single [CacheDiskUtils] instance
         */
        val instance: CacheDiskUtils
            get() = getInstance("", DEFAULT_MAX_SIZE, DEFAULT_MAX_COUNT)

        /**
         * 获取缓存实例
         *
         * cache directory: /data/data/package/cache/cacheUtils
         *
         * @param maxSize  The max size of cache, in bytes.
         * @param maxCount The max count of cache.
         * @return the single [CacheDiskUtils] instance
         */
        fun getInstance(maxSize: Long, maxCount: Int): CacheDiskUtils {
            return getInstance("", maxSize, maxCount)
        }

        /**
         * 获取缓存实例
         *
         * cache directory: /data/data/package/cache/cacheName
         *
         * @param cacheName The name of cache.
         * @param maxSize   The max size of cache, in bytes.
         * @param maxCount  The max count of cache.
         * @return the single [CacheDiskUtils] instance
         */
        @JvmOverloads
        fun getInstance(
            cacheName: String,
            maxSize: Long = DEFAULT_MAX_SIZE,
            maxCount: Int = DEFAULT_MAX_COUNT
        ): CacheDiskUtils {
            var cacheName = cacheName
            if (isSpace(cacheName)) cacheName = "cacheUtils"
            val file = File(AppManager.getInstance().getApp().getCacheDir(), cacheName)
            return getInstance(file, maxSize, maxCount)
        }

        /**
         * 获取缓存实例
         *
         * @param cacheDir The directory of cache.
         * @param maxSize  The max size of cache, in bytes.
         * @param maxCount The max count of cache.
         * @return the single [CacheDiskUtils] instance
         */
        @JvmOverloads
        fun getInstance(
            cacheDir: File,
            maxSize: Long = DEFAULT_MAX_SIZE,
            maxCount: Int = DEFAULT_MAX_COUNT
        ): CacheDiskUtils {
            val cacheKey = cacheDir.absoluteFile.toString() + "_" + maxSize + "_" + maxCount
            var cache = CACHE_MAP[cacheKey]
            if (cache == null) {
                cache = CacheDiskUtils(cacheKey, cacheDir, maxSize, maxCount)
                CACHE_MAP[cacheKey] = cache
            }
            return cache
        }

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////

        private fun string2Bytes(string: String?): ByteArray? {
            return string?.toByteArray()
        }

        private fun bytes2String(bytes: ByteArray?): String? {
            return if (bytes == null) null else String(bytes)
        }

        private fun jsonObject2Bytes(jsonObject: JSONObject?): ByteArray? {
            return jsonObject?.toString()?.toByteArray()
        }

        private fun bytes2JSONObject(bytes: ByteArray?): JSONObject? {
            if (bytes == null) return null
            try {
                return JSONObject(String(bytes))
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        private fun jsonArray2Bytes(jsonArray: JSONArray?): ByteArray? {
            return jsonArray?.toString()?.toByteArray()
        }

        private fun bytes2JSONArray(bytes: ByteArray?): JSONArray? {
            if (bytes == null) return null
            try {
                return JSONArray(String(bytes))
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        private fun parcelable2Bytes(parcelable: Parcelable?): ByteArray? {
            if (parcelable == null) return null
            val parcel = Parcel.obtain()
            parcelable.writeToParcel(parcel, 0)
            val bytes = parcel.marshall()
            parcel.recycle()
            return bytes
        }

        private fun <T> bytes2Parcelable(
            bytes: ByteArray?,
            creator: Parcelable.Creator<T>
        ): T? {
            if (bytes == null) return null
            val parcel = Parcel.obtain()
            parcel.unmarshall(bytes, 0, bytes.size)
            parcel.setDataPosition(0)
            val result = creator.createFromParcel(parcel)
            parcel.recycle()
            return result
        }

        private fun serializable2Bytes(serializable: Serializable?): ByteArray? {
            if (serializable == null) return null
            val baos: ByteArrayOutputStream
            var oos: ObjectOutputStream? = null
            try {
                baos = ByteArrayOutputStream()
                oos = ObjectOutputStream(baos)
                oos.writeObject(serializable)
                return baos.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    oos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        private fun bytes2Object(bytes: ByteArray?): Any? {
            if (bytes == null) return null
            var ois: ObjectInputStream? = null
            try {
                ois = ObjectInputStream(ByteArrayInputStream(bytes))
                return ois.readObject()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        private fun bitmap2Bytes(bitmap: Bitmap?): ByteArray? {
            if (bitmap == null) return null
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return baos.toByteArray()
        }

        private fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
            return if (bytes == null || bytes.size <= 0)
                null
            else
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        private fun drawable2Bytes(drawable: Drawable?): ByteArray? {
            return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable))
        }

        private fun bytes2Drawable(bytes: ByteArray?): Drawable? {
            return if (bytes == null) null else bitmap2Drawable(bytes2Bitmap(bytes))
        }

        private fun drawable2Bitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap
            if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                bitmap = Bitmap.createBitmap(
                    1,
                    1,
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

        private fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
            return if (bitmap == null)
                null
            else
                BitmapDrawable(AppManager.getInstance().getApp().resources, bitmap)
        }


        private fun writeFileFromBytes(file: File, bytes: ByteArray?) {
            var fc: FileChannel? = null
            try {
                fc = FileOutputStream(file, false).channel
                fc!!.write(ByteBuffer.wrap(bytes))
                fc.force(true)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fc?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        private fun readFile2Bytes(file: File): ByteArray? {
            var fc: FileChannel? = null
            try {
                fc = RandomAccessFile(file, "r").channel
                val size = fc!!.size().toInt()
                val mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size.toLong()).load()
                val data = ByteArray(size)
                mbb.get(data, 0, size)
                return data
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    fc?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
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
    }
}
/**
 * 获取缓存实例
 *
 * cache directory: /data/data/package/cache/cacheUtils
 *
 * cache size: unlimited
 *
 * cache count: unlimited
 *
 * @param cacheName The name of cache.
 * @return the single [CacheDiskUtils] instance
 */
/**
 * 获取缓存实例
 *
 * cache size: unlimited
 *
 * cache count: unlimited
 *
 * @param cacheDir The directory of cache.
 * @return the single [CacheDiskUtils] instance
 *////////////////////////////////////////////////////////////////////////////
// about bytes
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入字节数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 */
/**
 * 缓存中读取字节数组
 *
 * @param key The key of cache.
 * @return the bytes if cache exists or null otherwise
 *////////////////////////////////////////////////////////////////////////////
// about String
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入string数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 */
/**
 * 缓存中读取 String
 *
 * @param key The key of cache.
 * @return the string value if cache exists or null otherwise
 *////////////////////////////////////////////////////////////////////////////
// about JSONObject
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入json数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 */
/**
 * 缓存中读取 JSONObject
 *
 * @param key The key of cache.
 * @return the JSONObject if cache exists or null otherwise
 *////////////////////////////////////////////////////////////////////////////
// about JSONArray
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入jsonarray数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 */
/**
 * 缓存中读取 JSONArray
 *
 * @param key The key of cache.
 * @return the JSONArray if cache exists or null otherwise
 *////////////////////////////////////////////////////////////////////////////
// about Bitmap
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入bitmap数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 */
/**
 * 缓存中读取 Bitmap
 *
 * @param key The key of cache.
 * @return the bitmap if cache exists or null otherwise
 *////////////////////////////////////////////////////////////////////////////
// about Drawable
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入drawable数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 */
/**
 * 缓存中读取 Drawable
 *
 * @param key The key of cache.
 * @return the drawable if cache exists or null otherwise
 *////////////////////////////////////////////////////////////////////////////
// about Parcelable
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入parcelable数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 *////////////////////////////////////////////////////////////////////////////
// about Serializable
///////////////////////////////////////////////////////////////////////////
/**
 * 缓存中写入serializable数据
 *
 * @param key   The key of cache.
 * @param value The value of cache.
 */
/**
 * 缓存中读取 Serializable
 *
 * @param key The key of cache.
 * @return the bitmap if cache exists or null otherwise
 */