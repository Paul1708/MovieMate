package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbSearchResponse

interface OmdbRemoteDataSource {
    suspend fun searchMovies(title: String, year: String?): OmdbSearchResponse
    suspend fun fetchMovieDataByImdbId(imdbId: String): OmdbMovieDto?
}
