package com.example.pravki.repository

import com.example.pravki.retrofit.ApiService

class Repository(private val apiService: ApiService) {
    suspend fun getDiscover() = apiService.getDiscover()
    suspend fun getSearchDiscover(query: String) = apiService.getSearchDiscover(query)
}