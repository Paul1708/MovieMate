package de.fh.erfurt.moviemate.movies.data.local.datasource

import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.MovieLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalModel
import de.fh.erfurt.moviemate.movies.data.local.model.LocalMovieEntity

class SqlDelightMovieLocalDataSource(
    database: MovieMateDatabase,
    private val idPolicy: LocalIdPolicy = MovieLocalIdPolicy,
) : MovieLocalDataSource {

    private val queries = database.movieEntityQueries

    override suspend fun getMovies(): List<LocalMovieEntity> {
        return queries.selectAll().executeAsList().map { entity -> entity.toLocalModel() }
    }

    override suspend fun getMovie(movieId: String): LocalMovieEntity? {
        return queries.selectById(movieId).executeAsOneOrNull()?.toLocalModel()
    }

    override suspend fun insertMovie(movie: LocalMovieEntity): LocalMovieEntity {
        val resolvedMovie = movie.resolveIdIfNeeded()
        queries.insertMovie(
            resolvedMovie.id,
            resolvedMovie.title,
            resolvedMovie.description,
            resolvedMovie.releaseYear,
            resolvedMovie.categoryId,
            resolvedMovie.posterUrl,
            resolvedMovie.runtimeMinutes,
            resolvedMovie.isWatched,
            resolvedMovie.comment,
            resolvedMovie.userRating,
            resolvedMovie.imdbRating,
            resolvedMovie.externalIds,
            resolvedMovie.watchProviders,
            resolvedMovie.updatedAtEpochMillis,
            resolvedMovie.version,
        )
        return resolvedMovie
    }

    override suspend fun updateMovie(movie: LocalMovieEntity): LocalMovieEntity {
        require(movie.id.isNotBlank()) { "Movie id must not be blank when updating the local cache." }
        queries.updateMovie(
            movie.title,
            movie.description,
            movie.releaseYear,
            movie.categoryId,
            movie.posterUrl,
            movie.runtimeMinutes,
            movie.isWatched,
            movie.comment,
            movie.userRating,
            movie.imdbRating,
            movie.externalIds,
            movie.watchProviders,
            movie.updatedAtEpochMillis,
            movie.version,
            movie.id,
        )
        return movie
    }

    override suspend fun updateMovieWatchedStatus(movieId: String, isWatched: Boolean, updatedAtEpochMillis: Long) {
        require(movieId.isNotBlank()) { "Movie id must not be blank" }
        queries.updateMovieWatchedStatus(
            isWatched = if (isWatched) 1L else 0L,
            updatedAtEpochMillis = updatedAtEpochMillis,
            id = movieId,
        )
    }

    override suspend fun updateMovieUserRating(movieId: String, userRating: Int?, updatedAtEpochMillis: Long) {
        require(movieId.isNotBlank()) { "Movie id must not be blank" }
        queries.updateMovieUserRating(
            userRating = userRating?.toLong(),
            updatedAtEpochMillis = updatedAtEpochMillis,
            id = movieId,
        )
    }

    override suspend fun deleteMovie(movieId: String) {
        queries.deleteMovie(movieId)
    }

    override suspend fun searchMovies(query: String): List<LocalMovieEntity> {
        return queries.searchMovies(query).executeAsList().map { entity -> entity.toLocalModel() }
    }

    override suspend fun getMoviesByCategory(categoryId: String): List<LocalMovieEntity> {
        return queries.selectByCategoryId(categoryId).executeAsList().map { entity -> entity.toLocalModel() }
    }

    private suspend fun LocalMovieEntity.resolveIdIfNeeded(): LocalMovieEntity {
        if (id.isNotBlank()) return this
        val existingIds = getMovies().map { it.id }
        return copy(id = idPolicy.nextId(existingIds))
    }
}
