package com.azamovhudstc.soplay.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.imp.DetailRepositoryImpl
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.viewmodel.DetailViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class DetailViewModelImpl : ViewModel(), DetailViewModel {
    private val repo = DetailRepositoryImpl()
    override val movieDetailData: MutableLiveData<Resource<FullMovieData>> = MutableLiveData()
    override fun parseDetailByMovieInfo(movieInfo: MovieInfo) {
        movieDetailData.value = Resource.Loading
        repo.parseMovieDetailByHref(movieInfo).onEach {
            it.onSuccess {
                movieDetailData.value = Resource.Success(it)
            }
            it.onFailure {
                movieDetailData.value = Resource.Error(it)
            }
        }.launchIn(viewModelScope)
    }

}