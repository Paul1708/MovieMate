package de.fh.erfurt.moviemate.movies.data.local.datasource
import de.fh.erfurt.moviemate.movies.data.local.model.LocalMovieEntity

interface MovieLocalDataSource {
    suspend fun getMovies(): List<LocalMovieEntity>
    suspend fun getMovie(movieId: String): LocalMovieEntity?
    suspend fun insertMovie(movie: LocalMovieEntity): LocalMovieEntity
    suspend fun updateMovie(movie: LocalMovieEntity): LocalMovieEntity
    suspend fun deleteMovie(movieId: String)
    suspend fun searchMovies(query: String): List<LocalMovieEntity>
    suspend fun getMoviesByCategory(categoryId: String): List<LocalMovieEntity>
    suspend fun updateMovieWatchedStatus(movieId: String, isWatched: Boolean, updatedAtEpochMillis: Long)
    suspend fun updateMovieUserRating(movieId: String, userRating: Int?, updatedAtEpochMillis: Long)
}