package com.magicalrice.project.library_data.remote.http.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.*

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote.http.cookie.store
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description TODO
 */

class MemoryCookieStore :
    CookieStore {
    private val memoryCookies = HashMap<String, List<Cookie>>()

    override fun saveCookie(url: HttpUrl, cookie: Cookie) {
        val cookies = memoryCookies[url.host()]?.toMutableList()
        val needRemove = ArrayList<Cookie>()
        if (cookies != null) {
            for (item in cookies) {
                if (cookie.name() == item.name()) {
                    needRemove.add(item)
                }
            }
            cookies.removeAll(needRemove)
            cookies.add(cookie)
        }
    }

    override fun saveCookie(url: HttpUrl, cookie: List<Cookie>) {
        val oldCookies = memoryCookies[url.host()]?.toMutableList()
        val needRemove = ArrayList<Cookie>()
        if (oldCookies != null) {
            for (newCookie in cookie) {
                for (oldCookie in oldCookies) {
                    if (newCookie.name() == oldCookie.name()) {
                        needRemove.add(oldCookie)
                    }
                }
            }
            oldCookies.removeAll(needRemove)
            oldCookies.addAll(cookie)
        }
    }

    override fun loadCookie(url: HttpUrl): List<Cookie> {
        var cookies = memoryCookies[url.host()]
        if (cookies == null) {
            cookies = ArrayList()
            memoryCookies[url.host()] = cookies
        }
        return cookies
    }

    override fun getAllCookie(): List<Cookie> {
        val cookies = ArrayList<Cookie>()
        val httpUrls = memoryCookies.keys
        for (url in httpUrls) {
            cookies.addAll(memoryCookies[url]!!)
        }
        return cookies
    }

    override fun getCookie(url: HttpUrl): List<Cookie> {
        val cookies = ArrayList<Cookie>()
        val urlCookies = memoryCookies[url.host()]
        if (urlCookies != null) cookies.addAll(urlCookies)
        return cookies
    }

    override fun removeAllCookie(): Boolean {
        memoryCookies.clear()
        return true
    }

    override fun removeCookie(url: HttpUrl): Boolean {
        return memoryCookies.remove(url.host()) != null
    }

    override fun removeCookie(url: HttpUrl, cookie: Cookie): Boolean {
        val cookies = memoryCookies[url.host()]?.toMutableList()
        return cookies?.remove(cookie) ?: false
    }
}