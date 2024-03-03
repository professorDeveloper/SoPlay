package com.azamovhudstc.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.utils.Resource

interface HomeScreenViewModel {
    val loadPopularMovies: MutableLiveData<Resource<ArrayList<MovieInfo>>>
    val loadCheckList: MutableLiveData<ArrayList<Boolean>>
    val loadNeedWatch: MutableLiveData<ArrayList<MovieInfo>>
    val loadLastNews: MutableLiveData<ArrayList<MovieInfo>>
    val errorLiveData:MutableLiveData<String>
    fun isFavMovie(link:String)
    val isFavMovieData:MutableLiveData<Boolean>
    fun addFavMovie(movieInfo: MovieInfo)
    fun removeFavMovie(href:String)

    fun getLastNews()
    fun getNeedWatch()
    fun loadPopularMovies()


}