package com.di7ak.instalikes.net.insta

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.di7ak.instalikes.net.ApiRequest
import com.di7ak.instalikes.net.insta.models.*
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class InstaApi {
    companion object {
        private const val BASE_URI = "https://www.instagram.com/"
        private const val USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Mobile/14G60 Instagram 12.0.0.16.90 (iPhone9,4; iOS 10_3_3; en_US; en-US; scale=2.61; gamut=wide; 1080x1920)"
        private const val TIMEOUT = 25L

        lateinit var cookie: CookieManager
        val isLoggedIn get() = cookie.getCookie("ds_user_id") != null
        val userId get() = cookie.getCookie("ds_user_id")?.value()?.toLongOrNull() ?: 0
        var username
            get() = prefs.getString("username", "")!!
            set(value) = prefs.edit().putString("username", value).apply()
        private var twoFactorInfo: LoginModel.TwoFactorInfo? = null
        private var csrftoken: String = ""

        private lateinit var instaService: InstaApiService
        private lateinit var retrofit: Retrofit
        private val gson = GsonBuilder().setLenient().create()

        private lateinit var prefs: SharedPreferences

        fun init(context: Context) {
            prefs = context.getSharedPreferences("cookie", MODE_PRIVATE)
            val httpClient = OkHttpClient.Builder()
            httpClient.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.readTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.addInterceptor { chain ->
                val request =
                    chain.request().newBuilder()
                        .header("User-Agent", USER_AGENT)
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        .addHeader("Accept", "*/*")
                        .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                        //.addHeader("accept-encoding", "gzip, deflate, br")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("x-instagram-ajax", "1")
                        .addHeader("x-csrftoken", cookie.getCookie("csrftoken")?.value() ?: csrftoken)
                        .build()
                chain.proceed(request)
            }

            cookie = CookieManager(BASE_URI, context)
            httpClient.cookieJar(cookie)

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URI)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            instaService = retrofit.create(InstaApiService::class.java)
        }

        fun checkpoint(
            url: String,
            callback: (result: ChallengeModel?, error: Throwable?) -> Unit) {
            ApiRequest(instaService.checkpoint(url)).result { _, errorBodyCheckpoint ->
                if (errorBodyCheckpoint != null) {
                    val model = gson.fromJson<ChallengeModel>(
                        errorBodyCheckpoint.string(),
                        ChallengeModel::class.java
                    )
                    callback(model, null)
                } else {
                    ApiRequest(instaService.checkpointChoice(url, 0)).result { result, errorBody ->
                        val challengeModel = result ?: gson.fromJson<ChallengeModel>(
                            errorBody?.string(),
                            ChallengeModel::class.java
                        )
                        callback(challengeModel, null)
                    }.error {
                        callback(null, it)
                    }.execute()
                }
            }.error {
                callback(null, it)
            }.execute()
        }

        fun checkPointConfirmCode(challengeUrl: String, code: String, callback: (result: LoginModel?, error: Throwable?) -> Unit) {
            ApiRequest(
                instaService.checkpointConfirmCode(
                    challengeUrl,
                    code
                )
            ).result { result, errorBody ->
                val loginModel =
                    result ?: gson.fromJson<LoginModel>(errorBody?.string(), LoginModel::class.java)
                if (loginModel.authenticated) {
                    this.username = username
                }
                callback(loginModel, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun login(
            username: String,
            password: String,
            callback: (result: LoginModel?, error: Throwable?) -> Unit
        ) {
            cookie.clearCookies()

            ApiRequest(instaService.getHome()).result { _, _ ->
                //for csrftoken
                csrftoken = cookie.getCookie("csrftoken")?.value() ?: ""

                ApiRequest(instaService.login(username, password))
                    .result { result, errorBody ->
                        val loginModel = result ?: gson.fromJson<LoginModel>(
                            errorBody?.string(),
                            LoginModel::class.java
                        )
                        twoFactorInfo = loginModel?.twoFactorInfo
                        if (loginModel.authenticated) {
                            this.username = username
                            callback(loginModel, null)
                        } else callback(loginModel, null)
                    }.error {
                        callback(null, it)
                    }.execute()
            }.error {
                callback(null, it)
            }.execute()
        }

        fun confirmCode(code: String, callback: (result: LoginModel?, error: Throwable?) -> Unit) {
            ApiRequest(
                instaService.twoFactor(
                    twoFactorInfo!!.username,
                    code,
                    twoFactorInfo!!.identifier
                )
            ).result { result, errorBody ->
                val loginModel =
                    result ?: gson.fromJson<LoginModel>(errorBody?.string(), LoginModel::class.java)
                if (loginModel.authenticated) {
                    this.username = username
                }
                callback(loginModel, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun logout() {
            username = ""
            cookie.clearCookies()
        }

        fun getUserInfo(
            username: String,
            callback: (result: ProfileInfoModel?, error: Throwable?) -> Unit
        ) {
            ApiRequest(
                instaService.getProfileInfo(username)
            ).result { result, errorBody ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun getUserInfo(
            userId: Long,
            callback: (result: ProfileInfoModel.Graphql?, error: Throwable?) -> Unit
        ) {
            ApiRequest(
                instaService.getProfileInfo(userId)
            ).result { result, errorBody ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun getPost(
            shortcode: String,
            callback: (result: PostModel?, error: Throwable?) -> Unit
        ) {
            ApiRequest(
                instaService.getPost(shortcode)
            ).result { result, errorBody ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun getRequest(
            url: String,
            callback: (result: ResponseBody?, error: Throwable?) -> Unit
        ) {
            ApiRequest(
                instaService.getRequest(url)
            ).result { result, errorBody ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun getMedia(userId: Long, first: Int, after: String, callback: (result: ProfileInfoModel.Data?, error: Throwable?) -> Unit) {
            val variables = gson.toJson(MediaVariables(userId, first, after))
            ApiRequest(
                instaService.getMedia(variables)
            ).result { result, errorResponse ->
                callback(result, null)
            }.error {
                callback(null, it)
            }.execute()
        }

        fun like(objectId: Long, callback: (result: LikeModel?, error: Throwable?) -> Unit) {
            ApiRequest(
                instaService.like(objectId)
            ).result { result, errorResponse ->
                val errRes = errorResponse?.string()
                val likeModel = result ?: gson.fromJson<LikeModel>(
                    errRes,
                    LikeModel::class.java
                )
                callback(likeModel, null)
            }.error {
                callback(null, it)
            }.execute()
        }
    }
}

data class MediaVariables(
    var id: Long,
    var first: Int,
    var after: String
)