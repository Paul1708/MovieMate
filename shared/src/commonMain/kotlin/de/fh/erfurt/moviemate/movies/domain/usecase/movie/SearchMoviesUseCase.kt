package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class SearchMoviesUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(query: String): List<Movie> {
        validateSearchQuery(query)
        return movieRepository.searchMovies(query)
    }
}