package com.azamovhudstc.soplay.data.response

import java.io.Serializable

data class MovieInfo(
    val genre: String,
    val rating: String,
    val title: String,
    val image: String,
    val href: String,
    val quality: List<String>,
    val year: String
):Serializable