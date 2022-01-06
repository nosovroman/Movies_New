package com.example.pravki

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pravki.repository.Repository
import com.example.pravki.retrofit.ApiHelper

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MvvmViewModel::class.java)) {
            return MvvmViewModel(Repository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}