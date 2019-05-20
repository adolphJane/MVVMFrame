package com.magicalrice.project.library_base.advanced

import androidx.collection.LruCache
import com.magicalrice.project.library_base.base.constant.CacheConstants
import java.util.concurrent.ConcurrentHashMap

/**
 * @package com.magicalrice.project.library_base.advanced
 * @author Adolph
 * @date 2019-04-22 Mon
 * @description TODO
 */

class CacheMemoryUtils : CacheConstants {
    private val mCacheKey: String
    private val mMemoryCache: LruCache<String, CacheValue>

    constructor(cacheKey: String, memoryCache: LruCache<String, CacheValue>) {
        this.mCacheKey = cacheKey
        this.mMemoryCache = memoryCache
    }

    override fun toString(): String {
        return mCacheKey + "@" + Integer.toHexString(hashCode())
    }

    /**
     * 缓存中写入数据
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    fun put(key: String, value: Any) {
        put(key, value, -1)
    }

    /**
     * 缓存中写入数据
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    fun put(key: String, value: Any?, saveTime: Int) {
        if (value == null) return
        val dueTime = if (saveTime < 0) -1 else System.currentTimeMillis() + saveTime * 1000
        mMemoryCache.put(key, CacheValue(dueTime, value))
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key The key of cache.
     * @return the value if cache exists or null otherwise
     */
    operator fun <T> get(key: String): T? {
        return get<T>(key, null)
    }

    /**
     * 缓存中读取字节数组
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @return the value if cache exists or defaultValue otherwise
     */
    operator fun <T> get(key: String, defaultValue: T?): T? {
        val value = mMemoryCache.get(key) ?: return defaultValue
        if (value.dueTime == -1L || value.dueTime >= System.currentTimeMillis()) {
            return value.value as T?
        }
        mMemoryCache.remove(key)
        return defaultValue
    }

    /**
     * 获取缓存个数
     *
     * @return the count of cache
     */
    fun getCacheCount(): Int {
        return mMemoryCache.size()
    }

    /**
     * 根据键值移除缓存
     *
     * @param key The key of cache.
     * @return `true`: success<br></br>`false`: fail
     */
    fun remove(key: String): Any? {
        val remove = mMemoryCache.remove(key) ?: return null
        return remove.value
    }

    /**
     * 清除所有缓存
     */
    fun clear() {
        mMemoryCache.evictAll()
    }

    class CacheValue(var dueTime: Long, var value: Any)

    companion object {
        private val DEFAULT_MAX_COUNT = 256
        private val CACHE_MAP = ConcurrentHashMap<String, CacheMemoryUtils>()

        /**
         * 获取缓存实例
         *
         * @return the single [CacheMemoryUtils] instance
         */
        fun getInstance(): CacheMemoryUtils {
            return getInstance(DEFAULT_MAX_COUNT)
        }

        /**
         * 获取缓存实例
         *
         * @param maxCount The max count of cache.
         * @return the single [CacheMemoryUtils] instance
         */
        fun getInstance(maxCount: Int): CacheMemoryUtils {
            return getInstance(maxCount.toString(), maxCount)
        }

        /**
         * 获取缓存实例
         *
         * @param cacheKey The key of cache.
         * @param maxCount The max count of cache.
         * @return the single [CacheMemoryUtils] instance
         */
        fun getInstance(cacheKey: String, maxCount: Int): CacheMemoryUtils {
            var cache = CACHE_MAP[cacheKey]
            if (cache == null) {
                cache = CacheMemoryUtils(cacheKey, LruCache<String, CacheValue>(maxCount))
                CACHE_MAP[cacheKey] = cache
            }
            return cache
        }
    }
}