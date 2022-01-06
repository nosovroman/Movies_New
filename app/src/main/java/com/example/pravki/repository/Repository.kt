package com.example.pravki.repository

import com.example.pravki.retrofit.ApiHelper

class Repository(private val apiHelper: ApiHelper) {
    suspend fun getDiscover() = apiHelper.getDiscover()
}