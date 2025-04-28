package com.azamovme.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.utils.Resource

interface SearchViewModel {
    val searchData:MutableLiveData<Resource<ArrayList<MovieInfo>>>
    fun searchMovie(query:String)
    var loadPagingData: MutableLiveData<Resource<ArrayList<MovieInfo>>>
    var loadLastPagingData: MutableLiveData<Resource<ArrayList<MovieInfo>>>

    fun loadNextPage(page: Int)

    fun loadNextPageLast(page: Int)

}