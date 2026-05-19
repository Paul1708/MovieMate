package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

class GetMoviesByCategoryUseCase (
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(categoryId: String): List<Movie> {
        require(categoryId.isNotBlank()) { "Category ID cannot be empty" }
        return movieRepository.getMoviesByCategory(categoryId)
    }
}