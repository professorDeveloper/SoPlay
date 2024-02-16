package com.azamovhudstc.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.utils.Resource

interface DetailViewModel {
    fun parseDetailByMovieInfo(movieInfo: MovieInfo)
    fun loadPlayer(movieInfo: MovieInfo)
    fun addFavMovie(movieInfo: MovieInfo)
    fun removeFavMovie(href:String)
    fun isFavMovie(link:String)
    val isFavMovieData:MutableLiveData<Boolean>
    val movieDetailData: MutableLiveData<Resource<FullMovieData>>
    val playerData: MutableLiveData<FullMovieData>
}