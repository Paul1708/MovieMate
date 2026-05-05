package de.fh.erfurt.moviemate.domain.usecase

import de.fh.erfurt.moviemate.domain.repository.MovieRepository

class DeleteMovieUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: String) {
        require(id.isNotBlank()) { "The movie id cannot be blank." }
        repository.deleteMovie(id)
    }
}