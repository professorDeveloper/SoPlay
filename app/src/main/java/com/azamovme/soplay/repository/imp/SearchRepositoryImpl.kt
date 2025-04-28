package com.azamovme.soplay.repository.imp

import com.azamovme.soplay.app.App
import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.repository.SearchRepository
import com.azamovme.soplay.utils.Constants.mainUrl
import com.azamovme.soplay.utils.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jsoup.Connection
import org.jsoup.Jsoup

class SearchRepositoryImpl : SearchRepository {
    override fun searchMovies(query: String) = flow<Result<ArrayList<MovieInfo>>> {
        if (isOnline(App.instance)) {
            val movieList = ArrayList<MovieInfo>()
            val params = mapOf(
                "story" to query,
                "do" to "search",
                "subaction" to "search"
            )
            val headers = mapOf(
                "Accept" to "/*",
                "Host" to "asilmedia.org",
                "Cache-Control" to "no-cache",
                "Pragma" to "no-cache",

                "Connection" to "keep-alive",
                "Upgrade-Insecure-Requests" to "1",

                )
            val searchResponse = Jsoup.connect(mainUrl)
                .headers(headers)
                .data(params)
                .followRedirects(true)
                .method(Connection.Method.GET).execute().parse()

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