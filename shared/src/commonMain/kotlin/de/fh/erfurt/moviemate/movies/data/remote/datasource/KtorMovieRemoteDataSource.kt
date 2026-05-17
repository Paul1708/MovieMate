package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesApiConfiguration
import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateMovieRequestDto
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class KtorMovieRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiConfiguration: MoviesApiConfiguration

) : MovieRemoteDataSource {

    override suspend fun fetchMovies(): List<MovieDto> {
        return httpClient.get(apiConfiguration.moviesCollectionUrl()).body()
    }

    override suspend fun fetchMovie(movieId: String): MovieDto? {
        val response = httpClient.get(apiConfiguration.movieItemUrl(movieId))
        if (response.status == HttpStatusCode.NotFound) return null
        return response.body()
    }

    override suspend fun createMovie(request: CreateMovieRequestDto): MovieDto {
        return httpClient.post(apiConfiguration.moviesCollectionUrl()) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateMovie(movieId: String, request: UpdateMovieRequestDto): MovieDto {
        return httpClient.put(apiConfiguration.movieItemUrl(movieId)) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteMovie(movieId: String) {
        httpClient.delete(apiConfiguration.movieItemUrl(movieId))
    }

    override suspend fun searchMovies(query: String): List<MovieDto> {
        return httpClient.get(apiConfiguration.moviesCollectionUrl()) {
            parameter("query", query)
        }.body()
    }

    override suspend fun fetchMoviesByCategory(categoryId: String): List<MovieDto> {
        return httpClient.get(apiConfiguration.moviesCollectionUrl()) {
            parameter("categoryId", categoryId)
        }.body()
    }

    override suspend fun fetchMoviesSorted(sortBy: SortBy, ascending: Boolean): List<MovieDto> {
        return httpClient.get(apiConfiguration.moviesCollectionUrl()) {
            parameter("sortBy", sortBy.name)
            parameter("ascending", ascending)
        }.body()
    }
}