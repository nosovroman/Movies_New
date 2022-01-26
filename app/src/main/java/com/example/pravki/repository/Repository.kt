package com.example.pravki.repository

import androidx.lifecycle.LiveData
import com.example.pravki.retrofit.ApiService
import com.example.pravki.room.dao.FavoritesDao

class Repository(private val apiService: ApiService) {
    suspend fun getDiscover() = apiService.getDiscover()
    suspend fun getSearchDiscover(query: String) = apiService.getSearchDiscover(query)
}