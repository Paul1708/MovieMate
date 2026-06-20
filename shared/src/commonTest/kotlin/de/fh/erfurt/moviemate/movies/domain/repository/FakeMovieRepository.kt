package de.fh.erfurt.moviemate.movies.domain.repository

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy

class FakeMovieRepository(
    initialMovies: List<Movie> = emptyList()
) : MovieRepository {
    private val storedMovies = initialMovies.toMutableList()
    private var generatedIdCounter = initialMovies.size

    override suspend fun getMovies(): List<Movie> = storedMovies.toList()

    override suspend fun addMovie(draft: MovieDraft): Movie {
        generatedIdCounter++

        val createdMovie = Movie(
            id = generatedIdCounter.toString(),
            title = draft.title,
            description = draft.description,
            releaseYear = draft.releaseYear,
            categoryId = draft.categoryId,
            posterUrl = draft.posterUrl,
            runtimeMinutes = draft.runtimeMinutes,
            isWatched = draft.isWatched,
            comment = draft.comment,
            userRating = draft.userRating,
            imdbRating = draft.imdbRating,
            externalIds = draft.externalIds,
            watchProviders = draft.watchProviders,
            updatedAtEpochMillis = draft.updatedAtEpochMillis
        )
        storedMovies.add(createdMovie)
        return createdMovie
    }

    override suspend fun updateMovie(movie: Movie): Movie {
        val index = storedMovies.indexOfFirst { it.id == movie.id }
        check(index >= 0) {
            "Cannot update a movie that is not stored in the fake repository."
        }
        storedMovies[index] = movie
        return movie
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return storedMovies.filter { it.title.contains(query, ignoreCase = true) }
    }

    override suspend fun getMovie(movieId: String): Movie? {
        return storedMovies.find { it.id == movieId }
    }

    override suspend fun deleteMovie(movieId: String) {
        val index = storedMovies.indexOfFirst { it.id == movieId }
        check(index >= 0) {
            "Cannot delete a movie that is not stored in the fake repository."
        }
        storedMovies.removeAt(index)
    }

    override suspend fun getMoviesByCategory(categoryId: String): List<Movie> {
        return storedMovies.filter { it.categoryId == categoryId }
    }

    override suspend fun getMoviesSorted(sortBy: SortBy, ascending: Boolean): List<Movie> {
        val sorted = when (sortBy) {
            SortBy.TITLE -> storedMovies.sortedBy { it.title }
            SortBy.RELEASE_YEAR -> storedMovies.sortedBy { it.releaseYear }
            SortBy.RATING -> storedMovies.sortedBy { it.userRating }
            SortBy.UPDATED_AT -> storedMovies.sortedBy { it.updatedAtEpochMillis }
            SortBy.RUNTIME -> storedMovies.sortedBy { it.runtimeMinutes }
        }
        return if (ascending) sorted else sorted.reversed()
    }

    override suspend fun exportBackup(): String {
        // Für Testzwecke reicht eine minimale Implementierung oder ein vordefinierter String
        return "{ \"movies\": [], \"categories\": [] }"
    }

    override suspend fun importBackup(jsonString: String) {
        // Logik für Test-Imports falls benötigt, ansonsten leer belassen
    }

}