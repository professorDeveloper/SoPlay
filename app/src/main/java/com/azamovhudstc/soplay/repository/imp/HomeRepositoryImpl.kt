package com.azamovhudstc.soplay.repository.imp

import com.azamovhudstc.soplay.app.App
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.parser.extractMovieList
import com.azamovhudstc.soplay.repository.HomeRepository
import com.azamovhudstc.soplay.utils.Constants.mainUrl
import com.azamovhudstc.soplay.utils.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jsoup.Connection
import org.jsoup.Jsoup

class HomeRepositoryImpl : HomeRepository {

    override fun loadNextPage(page: Int) = flow<Result<ArrayList<MovieInfo>>> {
        if (isOnline(App.instance)) {

            val movieList = ArrayList<MovieInfo>()

            val searchResponse =
                Jsoup.connect("$mainUrl/films/tarjima_kinolar/page/$page/")
                    .followRedirects(true)
                    .headers(
                        mapOf(
                            "Content-Type" to "application/x-www-form-urlencoded",
                            "Accept" to "/*",
                            "Host" to "asilmedia.org",
                            "Cache-Control" to "no-cache",
                            "Pragma" to "no-cache",
                            "Connection" to "keep-alive",
                            "Upgrade-Insecure-Requests" to "1",

                            )
                    ).method(Connection.Method.GET).execute().parse()


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

            val searchResponse = Jsoup.connect("$mainUrl/popular.html")
                .followRedirects(true)
                .headers(
                    mapOf(
                        "Content-Type" to "application/x-www-form-urlencoded",
                        "Accept" to "/*",
                        "Host" to "asilmedia.org",
                        "Cache-Control" to "no-cache",
                        "Pragma" to "no-cache",
                        "Connection" to "keep-alive",
                        "Upgrade-Insecure-Requests" to "1",

                        )
                ).method(Connection.Method.GET).execute().parse()

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

    override fun getNeedWatch() = flow<Result<ArrayList<MovieInfo>>> {
        val document =
            Jsoup.connect("$mainUrl/whatchnow.html")
                .followRedirects(true)
                .headers(
                    mapOf(
                        "Content-Type" to "application/x-www-form-urlencoded",
                        "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                        "Host" to "asilmedia.org",
                        "Cache-Control" to "no-cache",
                        "Pragma" to "no-cache",
                        "Connection" to "keep-alive",
                        "Upgrade-Insecure-Requests" to "1",
                        "X-Requested-With" to "XMLHttpRequest"
                    )
                ).execute().parse()
        emit(Result.success(extractMovieList(document)))

    }.flowOn(Dispatchers.IO)

    override fun getLastPagination(page: Int) = flow<Result<ArrayList<MovieInfo>>> {
        val document =
            Jsoup.connect("$mainUrl/lastnews/page/$page")
                .followRedirects(true)
                .headers(
                    mapOf(
                        "Content-Type" to "application/x-www-form-urlencoded",
                        "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                        "Host" to "asilmedia.org",
                        "Cache-Control" to "no-cache",
                        "Pragma" to "no-cache",
                        "Connection" to "keep-alive",
                        "Upgrade-Insecure-Requests" to "1",
                        "X-Requested-With" to "XMLHttpRequest"
                    )
                ).execute().parse()


        emit(Result.success(extractMovieList(document)))
    }.flowOn(Dispatchers.IO)

    override fun getLastNews() = flow<Result<ArrayList<MovieInfo>>> {
        val document =
            Jsoup.connect(mainUrl + "/lastnews/")
                .followRedirects(true)
                .headers(
                    mapOf(
                        "Content-Type" to "application/x-www-form-urlencoded",
                        "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
                        "Host" to "asilmedia.org",
                        "Cache-Control" to "no-cache",
                        "Pragma" to "no-cache",
                        "Connection" to "keep-alive",
                        "Upgrade-Insecure-Requests" to "1",
                        "X-Requested-With" to "XMLHttpRequest"
                    )
                ).execute().parse()
        emit(Result.success(extractMovieList(document)))


    }.flowOn(Dispatchers.IO)
}
