package com.magicalrice.project.library_data.remote.http.interceptor.logging

import okhttp3.internal.platform.Platform
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote.http.interceptor.logging
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description TODO
 */

class I {
    private constructor() {
        throw UnsupportedOperationException()
    }

    companion object {
        fun log(type: Int, tag: String, msg: String) {
            val logger = Logger.getLogger(tag)
            when (type) {
                Platform.INFO -> logger.log(Level.INFO, msg)
                else -> logger.log(Level.WARNING, msg)
            }
        }
    }
}