package de.fh.erfurt.moviemate.movies.domain.repository

import de.fh.erfurt.moviemate.movies.domain.model.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.Movie
import de.fh.erfurt.moviemate.movies.domain.model.SortCriterion

interface MovieRepository {

    suspend fun getMovies(
        searchQuery: String? = null,
        categoryType: CategoryType? = null,
        isWatched: Boolean? = null,
        sortBy: SortCriterion = SortCriterion.RECENTLY_ADDED
    ): List<Movie>

    suspend fun addMovie(movie: Movie)
    suspend fun updateMovie(movie: Movie)
    suspend fun deleteMovie(id: String)

    suspend fun updateWatchStatus(id: String, isWatched: Boolean)
    suspend fun updateRating(id: String, rating: Int)
}