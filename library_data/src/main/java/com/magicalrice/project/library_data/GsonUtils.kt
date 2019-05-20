package com.magicalrice.project.library_data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.Reader
import java.lang.reflect.Type

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description Gson 相关
 *
 * getGson : 获取 Gson 对象
 * toJson  : 对象转 Json 串
 * fromJson: Json 串转对象
 */

object GsonUtils {
    private val GSON =
        createGson(true)

    private val GSON_NO_NULLS =
        createGson(false)

    /**
     * 获取 Gson 对象
     *
     * @return [Gson] instance.
     */
    fun getGson(): Gson {
        return getGson(true)
    }

    /**
     * 获取 Gson 对象
     *
     * @param serializeNulls determines if nulls will be serialized.
     * @return [Gson] instance.
     */
    fun getGson(serializeNulls: Boolean): Gson {
        return if (serializeNulls) GSON_NO_NULLS else GSON
    }

    /**
     * 对象转 Json 串
     *
     * @param object the object to serialize.
     * @return object serialized into json.
     */
    fun toJson(`object`: Any): String {
        return toJson(
            `object`,
            true
        )
    }

    /**
     * 对象转 Json 串
     *
     * @param object       the object to serialize.
     * @param includeNulls determines if nulls will be included.
     * @return object serialized into json.
     */
    fun toJson(`object`: Any, includeNulls: Boolean): String {
        return if (includeNulls) GSON.toJson(`object`) else GSON_NO_NULLS.toJson(`object`)
    }


    /**
     * Json 串转对象
     *
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(json: String, type: Class<T>): T {
        return GSON.fromJson(json, type)
    }

    /**
     * Json 串转对象
     *
     * @param json the json to convert.
     * @param type type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(json: String, type: Type): T {
        return GSON.fromJson(json, type)
    }

    /**
     * Json 串转对象
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(reader: Reader, type: Class<T>): T {
        return GSON.fromJson(reader, type)
    }

    /**
     * Json 串转对象
     *
     * @param reader the reader to convert.
     * @param type   type type json will be converted to.
     * @return instance of type
     */
    fun <T> fromJson(reader: Reader, type: Type): T {
        return GSON.fromJson(reader, type)
    }

    /**
     * Create a pre-configured [Gson] instance.
     *
     * @param serializeNulls determines if nulls will be serialized.
     * @return [Gson] instance.
     */
    private fun createGson(serializeNulls: Boolean): Gson {
        val builder = GsonBuilder()
        if (serializeNulls) builder.serializeNulls()
        return builder.create()
    }
}