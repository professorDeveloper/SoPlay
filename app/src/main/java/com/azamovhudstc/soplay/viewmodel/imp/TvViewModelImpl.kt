package com.azamovhudstc.soplay.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.soplay.data.Movie
import com.azamovhudstc.soplay.repository.imp.TvRepositoryImpl
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.viewmodel.TvViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TvViewModelImpl : ViewModel(), TvViewModel {
    private val repository = TvRepositoryImpl()
    private val isAutoPlayEnabled = true
    private val isVideoCacheEnabled = true

    val isLoading = MutableLiveData(true)
    val keepScreenOn = MutableLiveData(false)
    val showSubsBtn = MutableLiveData(false)
    val playNextEp = MutableLiveData(false)
    val isError = MutableLiveData(false)

    override val tvList: MutableLiveData<Resource<ArrayList<Movie>>> = MutableLiveData()
    override val hrefData: MutableLiveData<String> = MutableLiveData()

    override fun loadTv() {
        tvList.postValue(Resource.Loading)
        repository.getTv().onEach {
            it.onSuccess {
                tvList.postValue(Resource.Success(it))

            }
            it.onFailure {
                tvList.postValue(Resource.Error(it))

            }
        }.launchIn(viewModelScope)
    }

    override fun loadHrefData(movie: Movie) {
        viewModelScope.launch {
            hrefData.postValue(repository.getTvFullDataByHref(movie.href))
        }

    }
}