package com.azamovme.soplay.repository

import com.azamovme.soplay.data.response.MovieInfo
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchMovies(query: String): Flow<Result<ArrayList<MovieInfo>>>
}