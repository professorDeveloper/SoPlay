package com.azamovhudstc.soplay.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.imp.HomeRepositoryImpl
import com.azamovhudstc.soplay.repository.imp.SearchRepositoryImpl
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchViewModelImpl : SearchViewModel, ViewModel() {

    private val repository = SearchRepositoryImpl()
    private val homeRepository = HomeRepositoryImpl()
    var lastPage: Int = 1
    var isSearch = false
    var pagingData: ArrayList<MovieInfo> = arrayListOf()
    override val searchData: MutableLiveData<Resource<ArrayList<MovieInfo>>> = MutableLiveData()
    override var loadPagingData: MutableLiveData<Resource<ArrayList<MovieInfo>>> = MutableLiveData()

init{
    loadNextPage(1)
}
    override fun searchMovie(query: String) {
        searchData.postValue(Resource.Loading)
        repository.searchMovies(query).onEach {
            it.onSuccess {
                searchData.postValue(Resource.Success(it))
            }
            it.onFailure {
                searchData.postValue(Resource.Error(it))
            }
        }.launchIn(viewModelScope)
    }


    override fun loadNextPage(page: Int) {
        lastPage = page
        loadPagingData.postValue(Resource.Loading)
        homeRepository.loadNextPage(page).onEach {
            it.onSuccess {
                loadPagingData.postValue(Resource.Success(it))
            }
            it.onFailure {
                println(it.message)
                loadPagingData.postValue(Resource.Error(it))
            }
        }.launchIn(viewModelScope)
    }

}