package com.azamovhudstc.soplay.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.imp.HomeRepositoryImpl
import com.azamovhudstc.soplay.viewmodel.TrendingViewModel
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