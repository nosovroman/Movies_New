package com.example.arcticfoxcompose.dataClasses.DetailMovie

data class NeedDetailMovie(
    val genres: List<String> = listOf(),
    val title: String = "",
    val release_date: String = "",
    val vote_average: Double = 0.0,
    val video: Boolean = false,
    val overview: String = "",
)
