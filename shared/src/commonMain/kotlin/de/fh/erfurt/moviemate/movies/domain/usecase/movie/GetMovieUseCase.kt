package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class GetMovieUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(movieId: String): Movie? {
        require(movieId.isNotBlank()) { "Movie ID cannot be empty" }
        return movieRepository.getMovie(movieId)
    }
}