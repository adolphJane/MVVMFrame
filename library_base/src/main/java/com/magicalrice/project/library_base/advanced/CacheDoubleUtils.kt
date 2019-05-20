package com.magicalrice.project.library_base.advanced

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.magicalrice.project.library_base.base.constant.CacheConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

class CacheDoubleUtils : CacheConstants {
    private var mCacheMemoryUtils: CacheMemoryUtils
    private var mCacheDiskUtils: CacheDiskUtils

    constructor(cacheMemoryUtils: CacheMemoryUtils, cacheUtils: CacheDiskUtils) {
        mCacheMemoryUtils = cacheMemoryUtils
        mCacheDiskUtils = cacheUtils
    }

    ///////////////////////////////////////////////////////////////////////////
    // about bytes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: ByteArray?) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入字节数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: ByteArray?, saveTime: Int) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key The key of cache.
     * @return the bytes if cache exists or null otherwise
     */
    fun getBytes(key: String): ByteArray? {
        return getBytes(key, null)
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bytes if cache exists or defaultValue otherwise
     */
    fun getBytes(key: String, defaultValue: ByteArray?): ByteArray? {
        val obj = mCacheMemoryUtils.get<ByteArray>(key)
        return obj ?: mCacheDiskUtils.getBytes(key, defaultValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // about String
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入string数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: String) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入string数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: String, saveTime: Int) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 缓存中读取 String
     *
     * @param key The key of cache.
     * @return the string value if cache exists or null otherwise
     */
    fun getString(key: String): String? {
        return getString(key, null)
    }

    /**
     * 缓存中读取 String
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the string value if cache exists or defaultValue otherwise
     */
    fun getString(key: String, defaultValue: String?): String? {
        val obj = mCacheMemoryUtils.get<String>(key)
        return obj ?: mCacheDiskUtils.getString(key, defaultValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // about JSONObject
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入json数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: JSONObject) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入json数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(
        key: String,
        value: JSONObject,
        saveTime: Int
    ) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 缓存中读取 JSONObject
     *
     * @param key The key of cache.
     * @return the JSONObject if cache exists or null otherwise
     */
    fun getJSONObject(key: String): JSONObject? {
        return getJSONObject(key, null)
    }

    /**
     * 缓存中读取 JSONObject
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONObject if cache exists or defaultValue otherwise
     */
    fun getJSONObject(key: String, defaultValue: JSONObject?): JSONObject? {
        val obj = mCacheMemoryUtils.get<JSONObject>(key)
        return obj ?: mCacheDiskUtils.getJSONObject(key, defaultValue)
    }


    ///////////////////////////////////////////////////////////////////////////
    // about JSONArray
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入jsonarray数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: JSONArray) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入jsonarray数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: JSONArray, saveTime: Int) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 缓存中读取 JSONArray
     *
     * @param key The key of cache.
     * @return the JSONArray if cache exists or null otherwise
     */
    fun getJSONArray(key: String): JSONArray? {
        return getJSONArray(key, null)
    }

    /**
     * 缓存中读取 JSONArray
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the JSONArray if cache exists or defaultValue otherwise
     */
    fun getJSONArray(key: String, defaultValue: JSONArray?): JSONArray? {
        val obj = mCacheMemoryUtils.get<JSONArray>(key)
        return obj ?: mCacheDiskUtils.getJSONArray(key, defaultValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap cache
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入bitmap数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: Bitmap) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入bitmap数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: Bitmap, saveTime: Int) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 缓存中读取 Bitmap
     *
     * @param key The key of cache.
     * @return the bitmap if cache exists or null otherwise
     */
    fun getBitmap(key: String): Bitmap? {
        return getBitmap(key, null)
    }

    /**
     * 缓存中读取 Bitmap
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    fun getBitmap(key: String, defaultValue: Bitmap?): Bitmap? {
        val obj = mCacheMemoryUtils.get<Bitmap>(key)
        return obj ?: mCacheDiskUtils.getBitmap(key, defaultValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Drawable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入drawable数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: Drawable) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入drawable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: Drawable, saveTime: Int) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 缓存中读取 Drawable
     *
     * @param key The key of cache.
     * @return the drawable if cache exists or null otherwise
     */
    fun getDrawable(key: String): Drawable? {
        return getDrawable(key, null)
    }

    /**
     * 缓存中读取 Drawable
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the drawable if cache exists or defaultValue otherwise
     */
    fun getDrawable(key: String, defaultValue: Drawable?): Drawable? {
        val obj = mCacheMemoryUtils.get<Drawable>(key)
        return obj ?: mCacheDiskUtils.getDrawable(key, defaultValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Parcelable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入parcelable数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: Parcelable) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入parcelable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: Parcelable, saveTime: Int) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
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
        val value = mCacheMemoryUtils.get<T>(key)
        return value ?: mCacheDiskUtils.getParcelable(key, creator, defaultValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // about Serializable
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 缓存中写入serializable数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: Serializable) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入serializable数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: Serializable, saveTime: Int) {
        mCacheMemoryUtils.put(key, value, saveTime)
        mCacheDiskUtils.put(key, value, saveTime)
    }

    /**
     * 缓存中读取 Serializable
     *
     * @param key The key of cache.
     * @return the bitmap if cache exists or null otherwise
     */
    fun getSerializable(key: String): Any? {
        return getSerializable(key, null)
    }

    /**
     * 缓存中读取 Serializable
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the bitmap if cache exists or defaultValue otherwise
     */
    fun getSerializable(key: String, defaultValue: Any?): Any? {
        val obj = mCacheMemoryUtils.get<Any>(key)
        return obj ?: mCacheDiskUtils.getSerializable(key, defaultValue)
    }

    /**
     * 获取磁盘缓存大小
     *
     * @return the size of cache in disk
     */
    fun getCacheDiskSize(): Long {
        return mCacheDiskUtils.cacheSize
    }

    /**
     * 获取磁盘缓存个数
     *
     * @return the count of cache in disk
     */
    fun getCacheDiskCount(): Int {
        return mCacheDiskUtils.cacheCount
    }

    /**
     * 获取内存缓存个数
     *
     * @return the count of cache in memory.
     */
    fun getCacheMemoryCount(): Int {
        return mCacheMemoryUtils.getCacheCount()
    }

    /**
     * 根据键值移除缓存
     *
     * @param key The key of cache.
     */
    fun remove(key: String) {
        mCacheMemoryUtils.remove(key)
        mCacheDiskUtils.remove(key)
    }

    /**
     * 清除所有缓存
     */
    fun clear() {
        mCacheMemoryUtils.clear()
        mCacheDiskUtils.clear()
    }

    companion object {
        private val CACHE_MAP = ConcurrentHashMap<String, CacheDoubleUtils>()
        /**
         * 获取缓存实例
         *
         * @return the single [CacheDoubleUtils] instance
         */
        fun getInstance(): CacheDoubleUtils {
            return getInstance(CacheMemoryUtils.getInstance(), CacheDiskUtils.instance)
        }

        /**
         * 获取缓存实例
         *
         * @param cacheMemoryUtils The instance of [CacheMemoryUtils].
         * @param cacheDiskUtils   The instance of [CacheDiskUtils].
         * @return the single [CacheDoubleUtils] instance
         */
        fun getInstance(
            cacheMemoryUtils: CacheMemoryUtils,
            cacheDiskUtils: CacheDiskUtils
        ): CacheDoubleUtils {
            val cacheKey = cacheDiskUtils.toString() + "_" + cacheMemoryUtils.toString()
            var cache = CACHE_MAP[cacheKey]
            if (cache == null) {
                cache = CacheDoubleUtils(cacheMemoryUtils, cacheDiskUtils)
                CACHE_MAP[cacheKey] = cache
            }
            return cache
        }
    }
}