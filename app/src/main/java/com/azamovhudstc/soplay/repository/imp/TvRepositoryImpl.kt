package com.azamovhudstc.soplay.repository.imp

import com.azamovhudstc.soplay.data.Movie
import com.azamovhudstc.soplay.repository.TvRepository
import com.azamovhudstc.soplay.utils.Color
import com.azamovhudstc.soplay.utils.Utils.getJsoup
import com.azamovhudstc.soplay.utils.printlnColored
import com.azamovhudstc.soplay.utils.snackString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.jsoup.select.Elements

class TvRepositoryImpl : TvRepository {
    private val mainURL = "https://tas-ix.tv"

    override fun getTv() = flow<Result<ArrayList<Movie>>> {
        val doc = getJsoup(mainURL)
        val tvList = ArrayList<Movie>()
        val movieElements: Elements = doc.select(".tcarusel-item.main-news")
        if (movieElements != null) {
            // Select all links within the hidden-menu
            movieElements
                .map {
                    val href = it.select("a[href]").attr("href")
                    val title = it.select("a[href]").text()
                    val image = it.select("img.xfieldimage").attr("src")
                    val rating = it.select(".current-rating").text().toInt()
                    printlnColored("  Text: ${removeNumbers(title)}", Color.YELLOW)
                    printlnColored("  Image: $image", Color.DARK_ORANGE)
                    printlnColored("  Href: $href", Color.CYAN)
                    printlnColored("  Rating: $rating", Color.GREEN)
                    printlnColored("-----------------------------", Color.YELLOW)

                }
        } else {
            printlnColored("Uzbek channels not found.", com.azamovhudstc.soplay.utils.Color.YELLOW)
        }
        movieElements.map {
            val href = it.select("a[href]").attr("href")
            val title = it.select("a[href]").text()
            val image = it.select("img.xfieldimage").attr("src")
            val rating = it.select(".current-rating").text().toInt()

            tvList.add(Movie(href, removeNumbers(title), image, rating))
        }
        emit(Result.success(tvList))

    }.flowOn(Dispatchers.IO)

    override fun getNextTvPage(page: Int) = flow<Result<ArrayList<Movie>>> {
        val tvList = ArrayList<Movie>()
        val doc = getJsoup("$mainURL/page/$page/")
        val movieElements: Elements = doc.select(".tcarusel-item.main-news")
        if (movieElements != null) {
            movieElements
                .map {
                    val href = it.select("a[href]").attr("href")
                    val title = it.select("a[href]").text()
                    val image = it.select("img.xfieldimage").attr("src")
                    val rating = it.select(".current-rating").text().toInt()
                    printlnColored("  Text: ${removeNumbers(title)}", Color.YELLOW)
                    printlnColored("  Image: $image", Color.DARK_ORANGE)
                    printlnColored("  Href: $href", Color.CYAN)
                    printlnColored("  Rating: $rating", Color.GREEN)
                    printlnColored("-----------------------------", Color.YELLOW)

                }

        }
        movieElements.map {
            val href = it.select("a[href]").attr("href")
            val title = it.select("a[href]").text()
            val image = it.select("img.xfieldimage").attr("src")
            val rating = it.select(".current-rating").text().toInt()

            tvList.add(Movie(href, removeNumbers(title), image, rating))
        }
        emit(Result.success(tvList))
    }.flowOn(Dispatchers.IO)

    private fun removeNumbers(input: String): String {
        return input.replace(Regex("\\d+"), "").trim()
    }

    override suspend fun getTvFullDataByHref(href: String) = withContext(Dispatchers.IO) {

        try {
            val doc = getJsoup(href)
            val iframeElement = doc.select("iframe").first()
            val srcAttributeValue = iframeElement?.attr("src")
            val pattern = Regex("""file=([^&]+)""")

            if (iframeElement != null) {
                val matchResult = pattern.find(srcAttributeValue.toString())

                // Extract the value of the file parameter if a match is found
                val fileParameterValue = matchResult?.groups?.get(1)?.value

                if (fileParameterValue != null) {
                    println(fileParameterValue)
                    return@withContext (fileParameterValue)

                } else {
                    snackString("File parameter not found.")
                }
            }


        } catch (e: Exception) {
            printlnColored("SSL ERROR (Do`nt Worry)", Color.DARK_ORANGE)
        }
        return@withContext ""
    }

}