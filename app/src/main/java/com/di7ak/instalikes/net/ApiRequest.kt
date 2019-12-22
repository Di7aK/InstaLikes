package com.di7ak.instalikes.net

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class ApiRequest<T>(private val call: Call<T>) {
    private var result: ((result: T?, errorResponse: ResponseBody?) -> Unit)? = null
    private var error: ((error: Throwable) -> Unit)? = null

    fun result(result: (result: T?, errorResponse: ResponseBody?) -> Unit): ApiRequest<T> {
        this.result = result
        return this
    }

    fun error(error: (error: Throwable) -> Unit): ApiRequest<T> {
        this.error = error
        return this
    }

    fun abort() {
        call.cancel()
    }

    fun execute() : ApiRequest<T> {
        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                error?.invoke(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                try {
                    result?.invoke(response.body(), response.errorBody())
                } catch (e: Exception) {
                    error?.invoke(e)
                }
            }
        })
        return this
    }

}