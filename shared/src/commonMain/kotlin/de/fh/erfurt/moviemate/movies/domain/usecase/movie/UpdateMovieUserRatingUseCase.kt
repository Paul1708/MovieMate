package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class UpdateMovieUserRatingUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(movieId: String, userRating: Int?): Movie {
        require(movieId.isNotBlank()) { "Movie ID cannot be empty" }
        userRating?.let { rating -> require(rating in 1..10) { "User rating must be between 1 and 10" } }

        val movie = movieRepository.getMovie(movieId)
            ?: throw NoSuchElementException("Movie not found: $movieId")

        return movieRepository.updateMovie(
            movie = movie.copy(
                userRating = userRating,
            ),
        )
    }
}