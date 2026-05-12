package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.model.Category
import de.fh.erfurt.moviemate.movies.domain.model.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.MovieRepository

class GetMoviesByCategoryUseCase (
    private val repository: MovieRepository

) {
    suspend operator fun invoke(
        category: Category,
    ): List<Movie> {
        require(category.id.isNotBlank()) { "The category id cannot be blank." }
        return repository.getMoviesByCategory(category)
    }
}