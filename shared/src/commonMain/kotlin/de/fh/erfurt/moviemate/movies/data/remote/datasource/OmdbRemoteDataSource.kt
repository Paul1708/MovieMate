package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto

interface OmdbRemoteDataSource {
    suspend fun fetchMovieDataByTitle(title: String): OmdbMovieDto?
}
