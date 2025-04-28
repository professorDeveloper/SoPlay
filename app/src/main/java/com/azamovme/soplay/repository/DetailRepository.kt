package com.azamovme.soplay.repository

import com.azamovme.soplay.data.response.FullMovieData
import com.azamovme.soplay.data.response.MovieInfo
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    fun parseMovieDetailByHref(movieInfo: MovieInfo):Flow<Result<FullMovieData>>
}