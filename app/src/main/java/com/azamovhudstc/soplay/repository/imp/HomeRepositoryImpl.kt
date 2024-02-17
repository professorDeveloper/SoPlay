package com.azamovhudstc.soplay.repository.imp

import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.HomeRepository
import com.azamovhudstc.soplay.utils.Constants.mainUrl
import com.azamovhudstc.soplay.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

class HomeRepositoryImpl : HomeRepository {

    override fun loadNextPage(page: Int) = flow<Result<ArrayList<MovieInfo>>> {
        val movieList = ArrayList<MovieInfo>()

        val searchResponse = Utils.getJsoup(
            "$mainUrl/films/tarjima_kinolar/page/$page/",
        )

        val document = searchResponse
        val articles = document.select("article.shortstory-item")
        for (article in articles) {
            val genre = article.select("div.genre").text()
            val rating = article.select("span.ratingplus").text() ?: "+0"
            val title = article.select("header.moviebox-meta h2.title").text()
            val image = article.select("picture.poster-img img").attr("data-src")
            val href = article.select("a.flx-column-reverse").attr("href")
            val quality = article.select("div.badge-tleft span.is-first").eachText()
            val year = article.select("div.year a").text()

            val movieInfo = MovieInfo(genre, rating, title, image, href, quality, year)
            movieList.add(movieInfo)
        }

        emit(if (movieList.isNotEmpty()) Result.success(movieList) else Result.failure(Exception("Movie Not Found")))
    }.flowOn(Dispatchers.IO)

    override fun loadPopularMovies() = flow<Result<ArrayList<MovieInfo>>> {
        val movieList = ArrayList<MovieInfo>()

        val searchResponse = Utils.getJsoup(
            "$mainUrl/popular.html",
        )

        val document = searchResponse
        val articles = document.select("article.shortstory-item")
        for (article in articles) {
            val genre = article.select("div.genre").text()
            val rating = article.select("span.ratingplus").text() ?: "+0"
            val title = article.select("header.moviebox-meta h2.title").text()
            val image = article.select("picture.poster-img img").attr("data-src")
            val href = article.select("a.flx-column-reverse").attr("href")
            val quality = article.select("div.badge-tleft span.is-first").eachText()
            val year = article.select("div.year a").text()

            val movieInfo = MovieInfo(genre, rating, title, image, href, quality, year)
            movieList.add(movieInfo)
        }
        emit(if (movieList.isNotEmpty()) Result.success(movieList) else Result.failure(Exception("Movie Not Found")))
    }.flowOn(Dispatchers.IO)
}
