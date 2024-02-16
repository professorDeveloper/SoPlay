package com.azamovhudstc.soplay.repository.imp

import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.FavoriteRepository
import com.azamovhudstc.soplay.room.FavRoomModel
import com.azamovhudstc.soplay.room.LinkDao
import com.azamovhudstc.soplay.utils.Constants.SOURCE
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(private val dao: LinkDao) : FavoriteRepository {
    override suspend fun getFavoritesFromRoom() = flow<List<MovieInfo>> {
        dao.getLinks(SOURCE).collect {animeList->
            val list = animeList.map {
                MovieInfo(
                    it.favGenre,
                    it.favRating,
                    it.nameString,
                    it.picLinkString,
                    it.linkString,
                    listOf("720p"),
                    it.favYear
                )
            }
            emit(list)

        }
    }

    override suspend fun checkFavoriteFromRoom(animeLink: String, sourceName: String): Boolean {
        return dao.isItFav(animeLink, sourceName)
    }

    override suspend fun removeFavFromRoom(animeLink: String, sourceName: String) {
        val foundFav = dao.getFav(animeLink, sourceName)
        dao.deleteOne(foundFav)
    }

    override suspend fun addFavToRoom(favRoomModel: FavRoomModel) {
        dao.insert(favRoomModel)
    }
}