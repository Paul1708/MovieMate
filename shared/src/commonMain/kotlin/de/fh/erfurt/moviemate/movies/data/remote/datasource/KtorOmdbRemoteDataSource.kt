package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class KtorOmdbRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiKey: String,
) : OmdbRemoteDataSource {
    override suspend fun fetchMovieDataByTitle(title: String): OmdbMovieDto? {
        val response: OmdbMovieDto = httpClient.get("https://omdbapi.com") {
            parameter("apikey", apiKey)
            parameter("t", title)
            parameter("type", "movie")
        }.body()

        return response.takeIf { it.response == "True" }
    }
}
