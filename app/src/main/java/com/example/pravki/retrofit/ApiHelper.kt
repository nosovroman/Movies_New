package com.example.pravki.retrofit

class ApiHelper(private val apiService: ApiService) {

    suspend fun getDiscover() = apiService.getDiscover()
}