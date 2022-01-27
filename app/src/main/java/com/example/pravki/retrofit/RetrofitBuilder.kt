package com.example.pravki.retrofit

import com.example.pravki.common.Constants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitBuilder {
    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val apiService: ApiService = getRetrofit(Constants.BASE_URL).create(ApiService::class.java)
}