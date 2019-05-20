package com.magicalrice.project.library_data.remote.http.interceptor.logging

import okhttp3.internal.platform.Platform

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote.http.interceptor.logging
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description TODO
 */

interface Logger {
    fun log(level: Int, tag: String, msg: String)

    companion object {
        val DEFAULT: Logger = object :
            Logger {
            override fun log(level: Int, tag: String, msg: String) {
                Platform.get().log(level, msg, null)
            }
        }
    }
}