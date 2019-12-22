package com.di7ak.instalikes.net.sync

import com.di7ak.instalikes.net.ApiRequest
import com.di7ak.instalikes.net.sync.models.AddOrderModel
import com.di7ak.instalikes.net.sync.models.JobsModel
import com.di7ak.instalikes.net.sync.models.RedeemModel
import com.di7ak.instalikes.net.sync.models.SyncModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class SyncApi {
    companion object {
        private const val TIMEOUT = 25L

        private lateinit var service: SyncApiService
        private lateinit var retrofit: Retrofit
        private val gson = GsonBuilder().setLenient().create()

        fun init(baseUri: String) {
            val httpClient = OkHttpClient.Builder()
            httpClient.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.readTimeout(TIMEOUT, TimeUnit.SECONDS)

            retrofit = Retrofit.Builder()
                .baseUrl(baseUri)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            service = retrofit.create(SyncApiService::class.java)
        }

        fun sync(userId: Long, username: String, isPrivate: Boolean, callback: (result: SyncModel?, error: Throwable?) -> Unit) {
            ApiRequest(service.syncProfile(userId, "android", Locale.getDefault().country, isPrivate, "google", username)).result { result, _ ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun getJobs(userId: Long, callback: (result: JobsModel?, error: Throwable?) -> Unit) {
            ApiRequest(service.getJobs(userId)).result { result, _ ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun redeem(orderId: Long, userId: Long, callback: (result: RedeemModel?, error: Throwable?) -> Unit) {
            ApiRequest(service.redeem(orderId, userId)).result { result, _ ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun addOrder(userId: Long, order: String, callback: (result: AddOrderModel?, error: Throwable?) -> Unit) {
            ApiRequest(service.addOrder(userId, order)).result { result, _ ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }
    }
}