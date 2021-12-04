package com.example.pravki.retrofit

import com.example.pravki.common.Constants
import com.example.arcticfoxcompose.dataClasses.DetailMovie.DetailMovie
import com.example.pravki.dataClasses.Discover
import com.example.pravki.dataClasses.Gallery
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
        // получение общего списка фильмов
    @GET("discover/movie?api_key=${Constants.API_KEY}&language=${Constants.LANGUAGE}")
    fun getDiscover(): Call<Discover>
        // получение списка фильмов по запросу
    @GET("search/movie?api_key=${Constants.API_KEY}&language=${Constants.LANGUAGE}")
    fun getSearchDiscover(@Query("query") query: String): Call<Discover>
//
//        // получение галереи фильма /movie/{movie_id}/images
//    @GET("movie/{movie_id}/images?api_key=${Constants.API_KEY}&language=${Constants.LANGUAGE}")
//    fun getGallery(@Path("movie_id") movieId: Int): Call<Gallery>
//
//        // получение детальной информации о фильме
//    @GET("movie/{movie_id}?api_key=${Constants.API_KEY}&language=${Constants.LANGUAGE}")
//    fun getDetailMovie(@Path("movie_id") movieId: Int): Call<DetailMovie>
}