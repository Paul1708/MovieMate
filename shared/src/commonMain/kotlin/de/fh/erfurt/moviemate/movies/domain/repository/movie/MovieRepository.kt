package de.fh.erfurt.moviemate.movies.domain.repository.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft

interface MovieRepository {

    suspend fun getMovies(): List<Movie>

    suspend fun getMovie(movieId: String): Movie?

    suspend fun addMovie(draft: MovieDraft): Movie

    suspend fun updateMovie(movie: Movie): Movie

    suspend fun deleteMovie(movieId: String)

    // Repository-level search/sort APIs (Variant 2)
    suspend fun searchMovies(query: String): List<Movie>

    suspend fun getMoviesByCategory(categoryId: String): List<Movie>

    suspend fun getMoviesSorted(sortBy: SortBy = SortBy.TITLE, ascending: Boolean = true): List<Movie>

    suspend fun exportBackup(): String

    suspend fun importBackup(jsonString: String)
}
