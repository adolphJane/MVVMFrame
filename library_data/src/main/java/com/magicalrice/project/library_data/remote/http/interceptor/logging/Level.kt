package com.magicalrice.project.library_data.remote.http.interceptor.logging

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote.http.interceptor.logging
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description TODO
 */

enum class Level {
    /**
     * No logs.
     */
    NONE,
    /**
     *
     * Example:
     * <pre>`- URL
     * - Method
     * - Headers
     * - Body
    `</pre> *
     */
    BASIC,
    /**
     *
     * Example:
     * <pre>`- URL
     * - Method
     * - Headers
    `</pre> *
     */
    HEADERS,
    /**
     *
     * Example:
     * <pre>`- URL
     * - Method
     * - Body
    `</pre> *
     */
    BODY
}