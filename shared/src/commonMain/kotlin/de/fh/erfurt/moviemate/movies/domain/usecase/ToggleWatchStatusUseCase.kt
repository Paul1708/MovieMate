package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.model.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.MovieRepository

class ToggleWatchStatusUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        require(movie.id.isNotBlank()) { "The movie id cannot be blank." }
        val newStatus = !movie.isWatched
        repository.updateWatchStatus(movie.id, newStatus)
    }
}