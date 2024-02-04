package com.azamovhudstc.soplay.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.imp.SearchRepositoryImpl
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchViewModelImpl : SearchViewModel, ViewModel() {

    private val repository = SearchRepositoryImpl()

    override val searchData: MutableLiveData<Resource<ArrayList<MovieInfo>>> = MutableLiveData()

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

}