package com.azamovhudstc.soplay.parser

import com.azamovhudstc.soplay.data.response.MovieInfo
import org.jsoup.nodes.Document

fun extractMovieList(document: Document): ArrayList<MovieInfo> {
    val movieElements = document.select("article.shortstory-item")
    var newList = ArrayList<MovieInfo>()
    movieElements.map { element ->
        newList.add(
            extractMovieInfo(element)
        )
    }
    return newList
}

fun extractMovieInfo(element: org.jsoup.nodes.Element): MovieInfo {
    val title = element.select("h2.title.is-6.txt-ellipsis.mb-2").text()
    val year = element.select("div.year a").text()
    val genre = element.select("div.genre").text()
    val rating = element.select("span.ratingtypeplusminus.ratingplus").text()
    val image = element.select("img.img-fit").attr("data-src")
    val href = element.select("a[href]").attr("href")
    val quality = element.select("div.badge-tleft span.is-first").eachText()

    return MovieInfo(genre, rating, title, image, href, quality, year)
}
