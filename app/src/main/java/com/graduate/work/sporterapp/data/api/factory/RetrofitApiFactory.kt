package com.graduate.work.sporterapp.data.api.factory

import com.graduate.work.sporterapp.core.annotation.ApiUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitApiFactory {
    private val defaultApiUrl = "https://random-word-api.herokuapp.com/"

    fun <T> createInstance(clazz: Class<T>): T {
        val apiUrlAnnotation = clazz.annotations.find { it is ApiUrl } as ApiUrl?
        val url = apiUrlAnnotation?.url ?: defaultApiUrl
        return retrofit(url).create(clazz)
    }

    private fun retrofit(apiUrl: String) = Retrofit.Builder()
        .baseUrl(apiUrl)
        .client(okHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun okHttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}