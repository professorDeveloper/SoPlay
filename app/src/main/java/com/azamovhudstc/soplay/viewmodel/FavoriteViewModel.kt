package com.azamovhudstc.soplay.viewmodel

import androidx.lifecycle.MutableLiveData
import com.azamovhudstc.soplay.data.response.MovieInfo

interface FavoriteViewModel {
    var favoriteAnimeList: MutableLiveData<ArrayList<MovieInfo>>

    fun loadFav()

}