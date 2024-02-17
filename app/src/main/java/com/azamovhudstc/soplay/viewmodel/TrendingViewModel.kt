package com.azamovhudstc.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovhudstc.soplay.data.response.MovieInfo

interface TrendingViewModel {
    var trendingMovieList: MutableLiveData<ArrayList<MovieInfo>>

    fun loadTrendMovies()

}