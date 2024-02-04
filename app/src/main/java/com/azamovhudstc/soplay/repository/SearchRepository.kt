package com.azamovhudstc.soplay.repository

import com.azamovhudstc.soplay.data.response.MovieInfo
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchMovies(query: String): Flow<Result<ArrayList<MovieInfo>>>
}