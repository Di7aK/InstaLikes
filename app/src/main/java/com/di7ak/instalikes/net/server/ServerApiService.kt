package com.di7ak.instalikes.net.server

import com.di7ak.instalikes.net.server.models.ServerModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApiService {
    @GET("get_server_url")
    fun getServerUrl(
        @Query("user_id") userId: Long
    ) : Call<ServerModel>
}