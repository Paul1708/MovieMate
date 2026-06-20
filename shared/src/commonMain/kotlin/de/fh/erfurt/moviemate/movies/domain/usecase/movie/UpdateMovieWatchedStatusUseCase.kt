package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class UpdateMovieWatchedStatusUseCase(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(movieId: String, isWatched: Boolean): Movie {
        require(movieId.isNotBlank()) { "Movie ID cannot be empty" }

        val movie = movieRepository.getMovie(movieId)
            ?: throw NoSuchElementException("Movie not found: $movieId")

        return movieRepository.updateMovie(
            movie = movie.copy(
                isWatched = isWatched,
            ),
        )
    }
}