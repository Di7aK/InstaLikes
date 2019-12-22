package com.di7ak.instalikes.net.insta

import com.di7ak.instalikes.net.insta.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface InstaApiService {

    @GET("/")
    fun getHome() : Call<ResponseBody>

    @GET
    fun getRequest(@Url url: String) : Call<ResponseBody>

    @GET("{checkpoint}")
    fun checkpoint(
        @Path("checkpoint", encoded = true) checkpoint: String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @POST("{checkpoint}")
    fun checkpointChoice(
        @Path("checkpoint", encoded = true) checkpoint: String,
        @Field("choice") choice: Int
    ) : Call<ChallengeModel>

    @FormUrlEncoded
    @POST("{checkpoint}")
    fun checkpointConfirmCode(
        @Path("checkpoint", encoded = true) checkpoint: String,
        @Field("security_code") securityCode: String
    ) : Call<LoginModel>

    @GET("{username}/?__a=1")
    fun getProfileInfo(
        @Path("username") username: String
    ) : Call<ProfileInfoModel>

    @GET("{userId}/?__a=1")
    fun getProfileInfo(
        @Path("userId") userId: Long
    ) : Call<ProfileInfoModel.Graphql>

    @GET("p/{shortcode}/?__a=1")
    fun getPost(
        @Path("shortcode") shortcode: String
    ) : Call<PostModel>


    @GET("graphql/query/")
    fun getMedia(
        @Query("variables") variables: String,
        @Query("query_id") queryId: Long = 17888483320059182
    ) : Call<ProfileInfoModel.Data>

    @FormUrlEncoded
    @POST("web/likes/{object_id}/like/")
    fun like(@Path("object_id") objectId: Long,
             @Field("test") test: String = "test"
    ) : Call<LikeModel>

    @FormUrlEncoded
    @POST("accounts/login/ajax/")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ) : Call<LoginModel>

    @FormUrlEncoded
    @POST("accounts/login/ajax/two_factor/")
    fun twoFactor(
        @Field("username") username: String,
        @Field("verificationCode") verificationCode: String,
        @Field("identifier") identifier: String
    ) : Call<LoginModel>
}