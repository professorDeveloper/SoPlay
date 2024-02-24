package com.azamovhudstc.soplay.repository.imp

import com.azamovhudstc.soplay.app.App
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.HomeRepository
import com.azamovhudstc.soplay.utils.Constants.mainUrl
import com.azamovhudstc.soplay.utils.Utils
import com.azamovhudstc.soplay.utils.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HomeRepositoryImpl : HomeRepository {

    override fun loadNextPage(page: Int) = flow<Result<ArrayList<MovieInfo>>> {
        if (isOnline(App.instance)) {

            val movieList = ArrayList<MovieInfo>()

            val searchResponse = Utils.getJsoup(
                "$mainUrl/films/tarjima_kinolar/page/$page/",
                mapOfHeaders = mapOf(
                    "Accept" to "/*",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.101.76 Safari/537.36",
                    "Host" to "asilmedia.org",
                    "Cache-Control" to "no-cache",
                    "Pragma" to "no-cache",
                    "Connection" to "keep-alive",
                    "Upgrade-Insecure-Requests" to "1",

                    )
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

            emit(
                if (movieList.isNotEmpty()) Result.success(movieList) else Result.failure(
                    Exception(
                        "Movie Not Found"
                    )
                )
            )
        } else {
            emit(Result.failure(Exception("No Internet Connection")))
        }

    }.flowOn(Dispatchers.IO)

    override fun loadPopularMovies() = flow<Result<ArrayList<MovieInfo>>> {
        if (isOnline(App.instance)) {
            val movieList = ArrayList<MovieInfo>()

            val searchResponse = Utils.getJsoup(
                "$mainUrl/popular.html",
                mapOfHeaders = mapOf(
                    "Accept" to "/*",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.101.76 Safari/537.36",
                    "Host" to "asilmedia.org",
                    "Cache-Control" to "no-cache",
                    "Pragma" to "no-cache",
                    "Connection" to "keep-alive",
                    "Upgrade-Insecure-Requests" to "1",

                    )
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
            emit(
                if (movieList.isNotEmpty()) Result.success(movieList) else Result.failure(
                    Exception(
                        "Movie Not Found"
                    )
                )
            )

        } else {
            emit(Result.failure(Exception("No Internet Connection")))
        }
    }.flowOn(Dispatchers.IO)
}
