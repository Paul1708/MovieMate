package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

class KtorOmdbRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiKey: String,
) : OmdbRemoteDataSource {
    override suspend fun searchMovies(title: String, year: String?): OmdbSearchResponse {
        val response = request<OmdbSearchResponse>("s", title, year)
        return if (isFound(response.response, response.error)) response else OmdbSearchResponse()
    }

    override suspend fun fetchMovieDataByImdbId(imdbId: String): OmdbMovieDto? {
        val response = request<OmdbMovieDto>("i", imdbId)
        return response.takeIf { isFound(it.response, it.error) }
    }

    private suspend inline fun <reified T> request(field: String, value: String, year: String? = null): T {
        val response = httpClient.get("https://omdbapi.com") {
            parameter("apikey", apiKey)
            parameter(field, value)
            year?.let { parameter("y", it) }
            parameter("type", "movie")
        }
        check(response.status.isSuccess()) { "Movie lookup is unavailable. Please try again." }
        return response.body()
    }

    private fun isFound(response: String?, failure: String?): Boolean {
        if (response == "True") return true
        if (failure == "Movie not found!" || failure == "Incorrect IMDb ID.") return false
        if (failure == "Too many results.") error("Too many movies found. Try a longer title or add a year.")
        error("Movie lookup is unavailable. Please try again.")
    }
}
