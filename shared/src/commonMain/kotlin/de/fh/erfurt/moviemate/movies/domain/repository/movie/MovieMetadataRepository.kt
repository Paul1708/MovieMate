package de.fh.erfurt.moviemate.movies.domain.repository.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieSearchResults

interface MovieMetadataRepository {
    suspend fun searchMovies(title: String, year: String?): MovieSearchResults
    suspend fun fetchMovieDataByImdbId(imdbId: String): FetchedMovieData?
}
