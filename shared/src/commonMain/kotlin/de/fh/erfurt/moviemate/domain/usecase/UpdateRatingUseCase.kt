package de.fh.erfurt.moviemate.domain.usecase

import de.fh.erfurt.moviemate.domain.repository.MovieRepository

class UpdateRatingUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: String, rating: Int) {
        require(rating in 1..10) { "The rating must be between 1 and 5." }

        repository.updateRating(id, rating)
    }
}