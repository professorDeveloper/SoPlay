package com.azamovme.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovme.soplay.data.response.MovieInfo

interface TrendingViewModel {
    var trendingMovieList: MutableLiveData<ArrayList<MovieInfo>>

    fun loadTrendMovies()

}