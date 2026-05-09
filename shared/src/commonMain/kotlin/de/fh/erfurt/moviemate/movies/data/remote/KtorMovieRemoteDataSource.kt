package de.fh.erfurt.moviemate.movies.data.remote

import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateMovieRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateRatingRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateWatchStatusRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorMovieRemoteDataSource(
    private val client: HttpClient
) : MovieRemoteDataSource {

    private companion object {
        const val MOVIES_PATH = "movies"
    }

    override suspend fun fetchMovies(): List<MovieDto> {
        return client.get(MOVIES_PATH).body()
    }

    override suspend fun createMovie(request: CreateMovieRequestDto): MovieDto {
        return client.post(MOVIES_PATH) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateMovie(id: String, request: UpdateMovieRequestDto): MovieDto {
        return client.put("$MOVIES_PATH/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteMovie(id: String) {
        client.delete("$MOVIES_PATH/$id")
    }

    override suspend fun updateWatchStatus(
        id: String,
        request: UpdateWatchStatusRequestDto
    ): MovieDto {
        return client.patch("$MOVIES_PATH/$id/watch-status") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateRating(id: String, request: UpdateRatingRequestDto): MovieDto {
        return client.patch("$MOVIES_PATH/$id/rating") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}