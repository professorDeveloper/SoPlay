package com.azamovme.soplay.repository.imp

import com.azamovme.soplay.app.App
import com.azamovme.soplay.data.response.FullMovieData
import com.azamovme.soplay.data.response.MovieInfo
import com.azamovme.soplay.repository.DetailRepository
import com.azamovme.soplay.utils.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.jsoup.Connection
import org.jsoup.Jsoup

class DetailRepositoryImpl : DetailRepository {
    override fun parseMovieDetailByHref(movieInfo: MovieInfo) = flow<Result<FullMovieData>> {
        if (isOnline(App.instance)) {
            val document = Jsoup.connect(movieInfo.href)
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
            val year: String =
                document.select("div.fullmeta-item span.fullmeta-label:contains(Год) + span.fullmeta-seclabel a")
                    .text()
            val country: String =
                document.select("div.fullmeta-item span.fullmeta-label:contains(Страна) + span.fullmeta-seclabel a")
                    .text()
            val durationElement =
                document.selectFirst(".fullmeta-item .fullmeta-seclabel a")?.text()
            val duration = durationElement?.replace(" мин", "")

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
            val options = matches.mapNotNull {
                val value = it.groupValues[1]
                val text = it.groupValues[2]
                if (value != "http://yangi-kinolar.ru/vast/player.html?file=[xfvalue_kino_url]") {
                    Pair(text, value)
                } else {
                    null
                }
            }.toList()

            val imageUrls = document.select(".xfieldimagegallery img.lazyload[data-src]")
                .map { it.attr("data-src") }

            val descriptionElements = document.select("span[itemprop=description]")
            val nonRussianDescription = descriptionElements.text()


            val rating = document.select(".r-im.txt-bold500.pfrate-count").text()


            // Extract the video URL from the iframe inside the "cn-content" div
            val videoDiv = document.selectFirst("#cn-content")
            val iframeElement = videoDiv?.selectFirst("iframe")
            val videoUrl = iframeElement?.attr("src")
            val parsedUrl = parseUrl(videoUrl!!)
            //Buni Sindirish imkonsiz

            val data = FullMovieData(
                year = year,
                country = country,
                duration = "000",
                posterImageSrc = posterImageSrc,
                genres = genres,
                directors = directors,
                actors = actors,
                options = options.distinct().filterNot { it.second.toIntOrNull() != null },
                imageUrls = imageUrls,
                description = nonRussianDescription!!,
                videoUrl = parsedUrl!!,
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