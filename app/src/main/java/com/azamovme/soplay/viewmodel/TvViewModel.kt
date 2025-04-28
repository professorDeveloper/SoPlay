package com.azamovme.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovme.soplay.data.Movie
import com.azamovme.soplay.tv.stv.response.RandomTvResponse
import com.azamovme.soplay.utils.Resource

interface TvViewModel {
    val tvList: MutableLiveData<Resource<ArrayList<Movie>>>
    val hrefData :MutableLiveData<String>
    val tvRandomList:MutableLiveData<Resource<RandomTvResponse>>
    fun loadTv()
    fun loadTvNextPage(page:Int)
    fun loadRandomTvById(id:Int)
    fun loadHrefData(movie:Movie)


}