package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.model.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.Movie
import de.fh.erfurt.moviemate.movies.domain.model.SortCriterion
import de.fh.erfurt.moviemate.movies.domain.repository.MovieRepository

class GetMoviesUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(
        searchQuery: String? = null,
        categoryType: CategoryType? = null,
        isWatched: Boolean? = null,
        sortBy: SortCriterion = SortCriterion.RECENTLY_ADDED
    ): List<Movie> {
        return repository.getMovies(searchQuery, categoryType, isWatched, sortBy)
    }
}