package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbSearchResponse
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType

interface OmdbRemoteDataSource {
    suspend fun searchMovies(title: String, year: String?, mediaType: MediaType = MediaType.MOVIE): OmdbSearchResponse
    suspend fun fetchMovieDataByImdbId(imdbId: String, mediaType: MediaType = MediaType.MOVIE): OmdbMovieDto?
}
