package com.di7ak.instalikes.net.server

import com.di7ak.instalikes.net.ApiRequest
import com.di7ak.instalikes.net.server.models.ServerModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ServerApi {
    companion object {
        private const val BASE_URI = "https://instadivider.appsforall.su//"
        private const val TIMEOUT = 25L

        private var service: ServerApiService
        private val retrofit: Retrofit
        private val gson = GsonBuilder().setLenient().create()

        init {
            val httpClient = OkHttpClient.Builder()
            httpClient.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.readTimeout(TIMEOUT, TimeUnit.SECONDS)

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URI)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            service = retrofit.create(ServerApiService::class.java)
        }

        fun getServerUrl(userId: Long, callback: (result: ServerModel?, error: Throwable?) -> Unit) {
            ApiRequest(service.getServerUrl(userId)).result { result, _ ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }
    }
}