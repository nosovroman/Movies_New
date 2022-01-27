package com.example.pravki.retrofit


import com.example.pravki.common.Constants
import com.example.pravki.dataClasses.Discover
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
        // получение общего списка фильмов
    @GET("discover/movie?api_key=${Constants.API_KEY}&language=${Constants.LANGUAGE}")
    suspend fun getDiscover(): Response<Discover>
        // получение списка фильмов по запросу
    @GET("search/movie?api_key=${Constants.API_KEY}&language=${Constants.LANGUAGE}")
    suspend fun getSearchDiscover(@Query("query") query: String): Response<Discover>
}