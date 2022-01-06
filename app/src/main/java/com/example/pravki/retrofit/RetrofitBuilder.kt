package com.example.pravki.retrofit

import com.example.pravki.common.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitBuilder {
    private val client = OkHttpClient.Builder().build()

    fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    val apiService: ApiService = getRetrofit(Constants.BASE_URL).create(ApiService::class.java)
}