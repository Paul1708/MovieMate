package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbSearchResponse
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

class KtorOmdbRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiKey: String,
) : OmdbRemoteDataSource {
    override suspend fun searchMovies(title: String, year: String?, mediaType: MediaType): OmdbSearchResponse {
        val response = request<OmdbSearchResponse>("s", title, mediaType, year)
        return if (isFound(response.response, response.error)) response else OmdbSearchResponse()
    }

    override suspend fun fetchMovieDataByImdbId(imdbId: String, mediaType: MediaType): OmdbMovieDto? {
        val response = request<OmdbMovieDto>("i", imdbId, mediaType)
        return response.takeIf { isFound(it.response, it.error) }
    }

    private suspend inline fun <reified T> request(field: String, value: String, mediaType: MediaType, year: String? = null): T {
        val response = httpClient.get("https://omdbapi.com") {
            parameter("apikey", apiKey)
            parameter(field, value)
            year?.let { parameter("y", it) }
            parameter("type", mediaType.name.lowercase())
        }
        check(response.status.isSuccess()) { "Title lookup is unavailable. Please try again." }
        return response.body()
    }

    private fun isFound(response: String?, failure: String?): Boolean {
        if (response == "True") return true
        if (failure == "Movie not found!" || failure == "Incorrect IMDb ID.") return false
        if (failure == "Too many results.") error("Too many matches. Try a longer title or add a year.")
        error("Title lookup is unavailable. Please try again.")
    }
}
