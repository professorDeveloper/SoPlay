package com.azamovhudstc.soplay.viewmodel.imp

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.imp.DetailRepositoryImpl
import com.azamovhudstc.soplay.repository.imp.FavoriteRepositoryImpl
import com.azamovhudstc.soplay.room.FavRoomModel
import com.azamovhudstc.soplay.utils.Resource
import com.azamovhudstc.soplay.utils.snackString
import com.azamovhudstc.soplay.viewmodel.DetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModelImpl @Inject constructor(private val repositoryImpl: FavoriteRepositoryImpl) :
    ViewModel(), DetailViewModel {
    private val repo = DetailRepositoryImpl()
    var episode = MutableLiveData<Pair<String, String>?>(null)
    fun getEpisode(): LiveData<Pair<String, String>?> = episode
    override val movieDetailData: MutableLiveData<Resource<FullMovieData>> = MutableLiveData()
    override val playerData: MutableLiveData<FullMovieData> = MutableLiveData()
    val epChanged = MutableLiveData(true)
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


    override fun loadPlayer(movieInfo: MovieInfo) {
        repo.parseMovieDetailByHref(movieInfo).onEach {
            it.onSuccess {
                playerData.value = it
            }
            it.onFailure {
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

    override val isFavMovieData: MutableLiveData<Boolean> = MutableLiveData()
    fun setEpisode(ep: Pair<String, String>?, who: String) {
        episode.postValue(ep)
        MainScope().launch(Dispatchers.Main) {
            episode.value = null
        }
    }

    fun onEpisodeClick(
        media: FullMovieData,
        i: String,
        manager: FragmentManager,
        launch: Boolean = true,
        prevEp: String? = null
    ) {
        Handler(Looper.getMainLooper()).post {
            if (manager.findFragmentByTag("dialog") == null && !manager.isDestroyed) {
                if (media.options.find { it.second == i } != null) {
                } else {
                    snackString("Couldn't find episode : $i")
                    return@post
                }
            }
        }
    }

}