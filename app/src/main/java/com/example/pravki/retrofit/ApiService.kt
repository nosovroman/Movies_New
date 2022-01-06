package com.example.pravki.retrofit


import com.example.pravki.common.Constants
import com.example.pravki.dataClasses.Discover
import retrofit2.http.GET

interface ApiService {
        // получение общего списка фильмов
    @GET("discover/movie?api_key=${Constants.API_KEY}&language=${Constants.LANGUAGE}")
    suspend fun getDiscover(): Discover
}