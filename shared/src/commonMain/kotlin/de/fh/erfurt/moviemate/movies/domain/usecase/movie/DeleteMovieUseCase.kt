package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class DeleteMovieUseCase (
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(movieId: String) {
        require(movieId.isNotBlank()) { "Movie ID cannot be empty" }
        movieRepository.deleteMovie(movieId)
    }
}