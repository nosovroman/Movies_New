package com.example.pravki.dataClasses.generalMovie

import com.squareup.moshi.Json

data class PageMoviesInfo(
    val page: Int,
    val results: List<GeneralMovieInfo>,
    @field:Json(name = "total_pages") val totalPages: Int,
    @field:Json(name = "total_results") val totalResults: Int
)