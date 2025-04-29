package com.azamovme.soplay.repository

import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.room.FavRoomModel
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getFavoritesFromRoom(): Flow<List<MovieInfo>>
    suspend fun checkFavoriteFromRoom(animeLink: String?, sourceName: String): Boolean
    suspend fun removeFavFromRoom(animeLink: String?, sourceName: String)
    suspend fun addFavToRoom(favRoomModel: FavRoomModel)


}