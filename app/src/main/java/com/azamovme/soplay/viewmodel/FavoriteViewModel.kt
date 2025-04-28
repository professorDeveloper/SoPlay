package com.azamovme.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovme.soplay.data.response.MovieInfo

interface FavoriteViewModel {
    var favoriteAnimeList: MutableLiveData<ArrayList<MovieInfo>>

    fun loadFav()

}