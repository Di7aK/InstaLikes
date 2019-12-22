package com.di7ak.instalikes.net.insta

import android.content.Context
import android.content.Context.MODE_PRIVATE
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieManager(url: String, context: Context) : CookieJar {
    companion object {
        private const val PREF_NAME = "cookie_manager"
        private const val PREF_KEY = "cookie_string"
    }

    private var prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    private val cookie: MutableMap<String, Cookie> = mutableMapOf()

    private fun updateCookies() {
        val cookieString = cookie.map {
            "${it.key}=${it.value.value()}"
        }.joinToString(";")

        prefs.edit().putString(PREF_KEY, cookieString).apply()
    }

    fun clearCookies() {
        cookie.clear()
        prefs.edit().remove(PREF_KEY).apply()
    }

    init {
        val domain = HttpUrl.parse(url)!!.host()

        val cookieString = prefs.getString(PREF_KEY, "")!!
        cookieString.split(";").map {
            val cookieParts = it.split("=")
            if(cookieParts.size == 2) {
                val builder = Cookie.Builder().domain(domain).name(cookieParts[0]).value(cookieParts[1])
                cookie[cookieParts[0]] = builder.build()
            }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        cookies.forEach { new ->
            cookie[new.name()] = new
        }
        updateCookies()
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return cookie.map { it.value }.toMutableList()
    }

    fun getCookie(key: String) : Cookie? {
        return cookie[key]
    }

    fun getCookieString() : String {
        return cookie.map { "${it.value}=${it.value.value()}" }.joinToString("; ")
    }
}