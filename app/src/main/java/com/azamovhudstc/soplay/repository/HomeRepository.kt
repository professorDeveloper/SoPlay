package com.azamovhudstc.soplay.repository

import com.azamovhudstc.soplay.data.response.MovieInfo
import java.util.concurrent.Flow

interface HomeRepository {
    fun loadNextPage(page:Int):kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun loadPopularMovies():kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun getNeedWatch():kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun getLastPagination(page:Int):kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun getLastNews():kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>

}