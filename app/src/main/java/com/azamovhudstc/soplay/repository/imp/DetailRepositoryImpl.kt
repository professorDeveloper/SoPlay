package com.azamovhudstc.soplay.repository.imp

import com.azamovhudstc.soplay.app.App
import com.azamovhudstc.soplay.data.response.FullMovieData
import com.azamovhudstc.soplay.data.response.MovieInfo
import com.azamovhudstc.soplay.repository.DetailRepository
import com.azamovhudstc.soplay.utils.Utils
import com.azamovhudstc.soplay.utils.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DetailRepositoryImpl : DetailRepository {
    override fun parseMovieDetailByHref(movieInfo: MovieInfo) = flow<Result<FullMovieData>> {
        if (isOnline(App.instance)) {
            val document = Utils.getJsoup(movieInfo.href)
            val year: String =
                document.select("div.fullmeta-item span.fullmeta-label:contains(Год) + span.fullmeta-seclabel a")
                    .text()

            val country: String =
                document.select("div.fullmeta-item span.fullmeta-label:contains(Страна) + span.fullmeta-seclabel a")
                    .text()

            val duration: String =
                document.select("div.fullmeta-item span.fullmeta-label:contains(Продолжительность) + span.fullmeta-seclabel a")
                    .text()

            val posterImageSrc: String =
                document.select("div.poster picture.poster-img img.lazyload").attr("data-src")

            // Extracting information from the parsed HTML
            val genres: List<Pair<String, String>> =
                document.select("div.fullinfo-list span.list-label:contains(Жанры) + span a")
                    .map { Pair(it.text(), it.attr("href")) }

            val directors: List<Pair<String, String>> =
                document.select("div.fullinfo-list span.list-label:contains(Режиссер) + span a")
                    .map { Pair(it.text(), it.attr("href")) }

            val actors: List<Pair<String, String>> =
                document.select("div.fullinfo-list span.list-label:contains(Актеры) + span a")
                    .map { Pair(it.text(), it.attr("href")) }

            val pattern = Regex("""<option value="([^"]+)">(.*?)</option>""")

            // Find all matches in the HTML content
            val matches = pattern.findAll(document.html())

            // Process the matches
            val options = matches.map {
                val value = it.groupValues[1]
                val text = it.groupValues[2]
                Pair(text, value)
            }.toMutableList()

            val imageUrls = document.select(".xfieldimagegallery img.lazyload[data-src]")
                .map { it.attr("data-src") }

            val descriptionElements = document.select("span[itemprop=description]")
            val nonRussianDescription = descriptionElements.text()

            // Filter out paragraphs with Russian characters

            println("Description: $nonRussianDescription")

            // Extension function to check if a character is Russian
            val rating = document.select(".r-im.txt-bold500.pfrate-count").text()


//        options.forEachIndexed { index, pair ->
//            options[index] = Pair(pair.first, parseUrl(pair.second)!!)
//
//        }
            // Extract the video URL from the iframe inside the "cn-content" div
            val videoDiv = document.selectFirst("#cn-content")
            val iframeElement = videoDiv?.selectFirst("iframe")
            val data = FullMovieData(
                year = year,
                country = country,
                duration = duration,
                posterImageSrc = posterImageSrc,
                genres = genres,
                directors = directors,
                actors = actors,
                options = options.distinct().filterNot { it.second.toIntOrNull() != null },
                imageUrls = imageUrls,
                description = nonRussianDescription!!,
                videoUrl = "parsedUrl"!!,
                IMDB_rating = rating
            )
            emit(Result.success(data))
        } else {
            emit(Result.failure(Exception("Check Your Network")))
        }

    }.flowOn(Dispatchers.IO)

    private fun parseUrl(url: String): String? {
        // Split the URL using "?" as the delimiter
        val parts = url.split("?")

        // Check if there are two parts (base URL and parameters)
        if (parts.size == 2) {
            // Split the parameters using "&" as the delimiter
            val parameters = parts[1].split("&")

            // Find the parameter with "file=" prefix
            val fileParameter = parameters.find { it.startsWith("file=") }

            // Extract the value after "file=" prefix
            return fileParameter?.substringAfter("file=")
        }

        return url
    }

}