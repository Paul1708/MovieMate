package de.fh.erfurt.moviemate.movies.data.repository.local

import de.fh.erfurt.moviemate.movies.data.local.datasource.MovieLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.MovieLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.mapper.local.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalEntity
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy

class LocalMovieRepository(
    private val localDataSource: MovieLocalDataSource,
    private val idPolicy: LocalIdPolicy = MovieLocalIdPolicy,
) : MovieRepository {

    override suspend fun getMovies(): List<Movie> {
        return localDataSource.getMovies().map { entity -> entity.toDomainModel() }
    }

    override suspend fun getMovie(movieId: String): Movie? {
        return localDataSource.getMovie(movieId)?.toDomainModel()
    }

    override suspend fun addMovie(draft: MovieDraft): Movie {
        val existingIds = localDataSource.getMovies().map { it.id }
        val storedMovie = localDataSource.insertMovie(
            draft.toLocalEntity(idPolicy.nextId(existingIds)),
        )
        return storedMovie.toDomainModel()
    }

    override suspend fun updateMovie(movie: Movie): Movie {
        return localDataSource.updateMovie(movie.toLocalEntity()).toDomainModel()
    }

    override suspend fun deleteMovie(movieId: String) {
        localDataSource.deleteMovie(movieId)
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        return localDataSource.searchMovies(query).map { entity -> entity.toDomainModel() }
    }

    override suspend fun getMoviesByCategory(categoryId: String): List<Movie> {
        return localDataSource.getMoviesByCategory(categoryId).map { entity -> entity.toDomainModel() }
    }

    override suspend fun getMoviesSorted(sortBy: SortBy, ascending: Boolean): List<Movie> {
        val comparator = when (sortBy) {
            SortBy.TITLE -> compareBy<Movie> { it.title.lowercase() }
            SortBy.RELEASE_YEAR -> compareBy<Movie> { it.releaseYear ?: Int.MIN_VALUE }
            SortBy.UPDATED_AT -> compareBy<Movie> { it.updatedAtEpochMillis }
            SortBy.RATING -> compareBy<Movie> { it.userRating ?: Int.MIN_VALUE }
            SortBy.RUNTIME -> compareBy<Movie> { it.runtimeMinutes ?: Int.MIN_VALUE }
        }

        val sortedMovies = getMovies().sortedWith(comparator)
        return if (ascending) sortedMovies else sortedMovies.reversed()
    }

    suspend fun updateMovieWatchedStatus(movieId: String, isWatched: Boolean, updatedAtEpochMillis: Long) {
        localDataSource.updateMovieWatchedStatus(movieId, isWatched, updatedAtEpochMillis)
    }

    suspend fun updateMovieUserRating(movieId: String, userRating: Int?, updatedAtEpochMillis: Long) {
        localDataSource.updateMovieUserRating(movieId, userRating, updatedAtEpochMillis)
    }
}