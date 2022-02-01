package com.example.pravki.z_naLuchieVremena

import java.lang.Exception

sealed class StateOfLoad<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): StateOfLoad<T>(data)
    class Error<T>(message: String, data: T? = null): StateOfLoad<T>(data, message)
}
