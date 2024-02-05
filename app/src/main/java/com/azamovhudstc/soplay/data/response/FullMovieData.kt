package com.azamovhudstc.soplay.data.response


data class FullMovieData(

    val year: String,
    val country: String,
    val duration: String,
    val posterImageSrc: String,
    val genres: List<Pair<String, String>>,
    val directors: List<Pair<String, String>>,
    val actors: List<Pair<String, String>>,
    val options: List<Pair<String, String>>,
    val imageUrls: List<String>,
    val description: String,
    val videoUrl: String,
    val IMDB_rating: String
)

{
    override fun toString(): String {
        return """
            Year: $year
            Country: $country
            Duration: $duration
            Poster Image Src: $posterImageSrc
            
            Genres:
            ${genres.joinToString("\n") { "${it.first} - ${it.second}" }}
            
            Directors:
            ${directors.joinToString("\n") { "${it.first} - ${it.second}" }}
            
            Actors:
            ${actors.joinToString("\n") { "${it.first} - ${it.second}" }}
            
            Options:
            ${options.joinToString("\n") { "${it.first} - ${it.second}" }}
            
            Image URLs:
            ${imageUrls.joinToString("\n")}
            
            Description: $description
            
            Video URL: $videoUrl
        """.trimIndent()
    }

}