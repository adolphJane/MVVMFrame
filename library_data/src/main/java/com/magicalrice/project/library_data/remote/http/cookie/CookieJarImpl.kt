package com.magicalrice.project.library_data.remote.http.cookie

import com.magicalrice.project.library_data.remote.http.cookie.store.CookieStore
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote.http.cookie
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description TODO
 */

class CookieJarImpl : CookieJar {
    private val cookieStore: CookieStore

    constructor(cookieStore: CookieStore) {
        if (cookieStore == null) {
            throw IllegalArgumentException("cookieStore can not be null!")
        }
        this.cookieStore = cookieStore
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        cookieStore.saveCookie(url, cookies)
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return cookieStore.loadCookie(url).toMutableList()
    }

    fun getCookieStore(): CookieStore {
        return cookieStore
    }
}