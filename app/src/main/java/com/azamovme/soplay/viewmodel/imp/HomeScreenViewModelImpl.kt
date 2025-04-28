package com.azamovme.soplay.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.repository.imp.FavoriteRepositoryImpl
import com.azamovme.soplay.repository.imp.HomeRepositoryImpl
import com.azamovme.soplay.room.FavRoomModel
import com.azamovme.soplay.utils.Resource
import com.azamovme.soplay.viewmodel.HomeScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModelImpl @Inject constructor(private val repositoryImpl: FavoriteRepositoryImpl) :
    ViewModel(), HomeScreenViewModel {
    private val homeRepository = HomeRepositoryImpl()
    override val loadPopularMovies: MutableLiveData<Resource<ArrayList<MovieInfo>>> =
        MutableLiveData()
    override val loadNeedWatch: MutableLiveData<ArrayList<MovieInfo>> = MutableLiveData()
    override val loadLastNews: MutableLiveData<ArrayList<MovieInfo>> = MutableLiveData()
    override val errorLiveData: MutableLiveData<String> = MutableLiveData()
    override val isFavMovieData: MutableLiveData<Boolean> = MutableLiveData()
    override val loadCheckList: MutableLiveData<ArrayList<Boolean>> = MutableLiveData()

    override fun removeFavMovie(href: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.removeFavFromRoom(href, "asilmedia")
            isFavMovieData.postValue(false)
        }
    }

    override fun isFavMovie(link: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isFavMovieData.postValue(
                repositoryImpl.checkFavoriteFromRoom(link, "asilmedia")
            )
        }
    }


    override fun getLastNews() {
        homeRepository.getLastNews().onEach {
            it.onSuccess {
                loadLastNews.postValue(it)
            }
            it.onFailure {
                errorLiveData.postValue(it.message)
            }
        }.launchIn(viewModelScope)
    }

    override fun addFavMovie(movieInfo: MovieInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryImpl.addFavToRoom(
                FavRoomModel(
                    movieInfo.href,
                    movieInfo.rating,
                    movieInfo.year,
                    movieInfo.genre,
                    movieInfo.image,
                    movieInfo.title,
                    "asilmedia"
                )
            )
            isFavMovieData.postValue(true)
        }
    }

    override fun getNeedWatch() {
        homeRepository.getNeedWatch().onEach {
            it.onFailure {
                errorLiveData.postValue(it.message)
            }
            it.onSuccess {
                loadNeedWatch.postValue(it)
            }

        }.launchIn(viewModelScope)
    }

    override fun loadPopularMovies() {
        loadPopularMovies.postValue(Resource.Loading)
        homeRepository.loadPopularMovies().onEach {
            it.onSuccess {
                val checkList = ArrayList<Boolean>()
                it.onEach {
                    viewModelScope.launch (Dispatchers.IO){
                        checkList.add(
                            repositoryImpl.checkFavoriteFromRoom(it.href, "asilmedia")
                        )
                    }
                }

                loadPopularMovies.postValue(Resource.Success(it))
                loadCheckList.postValue(checkList)
            }
            it.onFailure {
                loadPopularMovies.postValue(Resource.Error(it))
            }
        }.launchIn(viewModelScope)

    }
}