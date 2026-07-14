package de.fh.erfurt.moviemate.movies.domain.repository.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData

interface MovieMetadataRepository {
    suspend fun fetchMovieDataByTitle(title: String): FetchedMovieData?
}
