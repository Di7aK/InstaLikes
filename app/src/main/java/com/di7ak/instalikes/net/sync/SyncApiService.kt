package com.di7ak.instalikes.net.sync

import com.di7ak.instalikes.net.sync.models.AddOrderModel
import com.di7ak.instalikes.net.sync.models.JobsModel
import com.di7ak.instalikes.net.sync.models.RedeemModel
import com.di7ak.instalikes.net.sync.models.SyncModel
import retrofit2.Call
import retrofit2.http.*

interface SyncApiService {
    @GET("instausers/{userId}/sync_profile")
    fun syncProfile(
        @Path("userId") userId: Long,
        @Query("platform") platform: String,
        @Query("locale") locale: String,
        @Query("isPrivate") isPrivate: Boolean,
        @Query("source") source: String,
        @Query("instaname") username: String,
        @Query("v") version: Int = 27
    ) : Call<SyncModel>

    @GET("instausers/{userId}/get_jobs_likes")
    fun getJobs(
        @Path("userId") userId: Long
    ) : Call<JobsModel>

    @FormUrlEncoded
    @POST("order_likes/{orderId}/redeem")
    fun redeem(
        @Path("orderId") orderId: Long,
        @Field("liker_id") likerId: Long
    ) : Call<RedeemModel>

    @FormUrlEncoded
    @POST("order_likes/add")
    fun addOrder(
        @Field("owner_id") ownerId: Long,
        @Field("order") order: String
    ) : Call<AddOrderModel>
}