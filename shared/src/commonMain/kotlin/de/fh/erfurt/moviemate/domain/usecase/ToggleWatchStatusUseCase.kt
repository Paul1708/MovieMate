package de.fh.erfurt.moviemate.domain.usecase

import de.fh.erfurt.moviemate.domain.model.Movie
import de.fh.erfurt.moviemate.domain.repository.MovieRepository

class ToggleWatchStatusUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        val newStatus = !movie.isWatched
        repository.updateWatchStatus(movie.id, newStatus)
    }
}