package de.fh.erfurt.moviemate.movies.data.remote.client

data class MoviesApiConfiguration(
    private val rawBaseUrl: String,
    )
{
    init {
        require(rawBaseUrl.isNotBlank()) {
            "The movie API base URL must not be blank."
        }
    }

    private val normalizedBaseUrl = rawBaseUrl.trimEnd('/')

    fun moviesCollectionUrl(): String = "$normalizedBaseUrl/movies"

    fun movieItemUrl(movieId: String): String = "${moviesCollectionUrl()}/$movieId"

    fun categoriesCollectionUrl(): String = "$normalizedBaseUrl/categories"

    fun categoryItemUrl(categoryId: String): String = "${categoriesCollectionUrl()}/$categoryId"


}
