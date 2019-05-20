package com.magicalrice.project.library_data.remote.http.cookie.store

import okhttp3.Cookie
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * @package com.magicalrice.project.library_third_internal.storage.remote.http.cookie.store
 * @author Adolph
 * @date 2019-04-24 Wed
 * @description TODO
 */

class SerializableHttpCookie : Serializable {
    @Transient
    private var cookie: Cookie
    @Transient
    private var clientCookie: Cookie? = null

    constructor(cookie: Cookie) {
        this.cookie = cookie
    }

    fun getCookie(): Cookie {
        var bestCookie = cookie
        if (clientCookie != null) {
            bestCookie = clientCookie!!
        }
        return bestCookie
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.name())
        out.writeObject(cookie.value())
        out.writeLong(cookie.expiresAt())
        out.writeObject(cookie.domain())
        out.writeObject(cookie.path())
        out.writeBoolean(cookie.secure())
        out.writeBoolean(cookie.httpOnly())
        out.writeBoolean(cookie.hostOnly())
        out.writeBoolean(cookie.persistent())
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(inputStream: ObjectInputStream) {
        val name = inputStream.readObject() as String
        val value = inputStream.readObject() as String
        val expiresAt = inputStream.readLong()
        val domain = inputStream.readObject() as String
        val path = inputStream.readObject() as String
        val secure = inputStream.readBoolean()
        val httpOnly = inputStream.readBoolean()
        val hostOnly = inputStream.readBoolean()
        val persistent = inputStream.readBoolean()
        var builder = Cookie.Builder()
        builder = builder.name(name)
        builder = builder.value(value)
        builder = builder.expiresAt(expiresAt)
        builder = if (hostOnly) builder.hostOnlyDomain(domain) else builder.domain(domain)
        builder = builder.path(path)
        builder = if (secure) builder.secure() else builder
        builder = if (httpOnly) builder.httpOnly() else builder
        clientCookie = builder.build()
    }

    companion object {
        private const val serialVersionUID = 6374381323722046732L
    }
}