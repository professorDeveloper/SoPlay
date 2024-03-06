package com.azamovhudstc.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovhudstc.soplay.data.Movie
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.utils.Resource

interface TvViewModel {
    val tvList: MutableLiveData<Resource<ArrayList<Movie>>>
    val hrefData :MutableLiveData<String>
    fun loadTv()
    fun loadTvNextPage(page:Int)
    fun loadHrefData(movie:Movie)


}