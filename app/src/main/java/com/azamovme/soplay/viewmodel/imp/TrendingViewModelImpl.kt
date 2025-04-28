package com.azamovme.soplay.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.repository.imp.HomeRepositoryImpl
import com.azamovme.soplay.viewmodel.TrendingViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrendingViewModelImpl : TrendingViewModel, ViewModel() {
    private val repositoryImpl = HomeRepositoryImpl()
    override var trendingMovieList: MutableLiveData<ArrayList<MovieInfo>> = MutableLiveData()

    override fun loadTrendMovies() {

        repositoryImpl.loadPopularMovies().onEach {
            it.onSuccess {
                trendingMovieList.postValue(it)
            }
            it.onFailure {
            }
        }.launchIn(viewModelScope)
    }
}