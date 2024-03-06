package com.azamovhudstc.soplay.repository

import com.azamovhudstc.soplay.data.Movie
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    fun getTv(): Flow<Result<ArrayList<Movie>>>
    fun getNextTvPage(page:Int): Flow<Result<ArrayList<Movie>>>

  suspend  fun getTvFullDataByHref(href: String): String
}