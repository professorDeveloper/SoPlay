package com.azamovhudstc.soplay.repository

import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun parseMovieDetailByHref(movieInfo: MovieInfo):Flow<Result<FullMovieData>>
}