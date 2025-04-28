package com.azamovme.soplay.repository

import com.azamovme.soplay.data.response.MovieInfo

interface HomeRepository {
    fun loadNextPage(page:Int):kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun loadPopularMovies():kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun getNeedWatch():kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun getLastPagination(page:Int):kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>
    fun getLastNews():kotlinx.coroutines.flow.Flow<Result<ArrayList<MovieInfo>>>

}