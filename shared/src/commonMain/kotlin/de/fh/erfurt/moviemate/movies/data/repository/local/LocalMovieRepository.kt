package de.fh.erfurt.moviemate.movies.data.repository.local

import de.fh.erfurt.moviemate.movies.data.local.datasource.CategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.datasource.MovieLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.MovieLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.model.LocalCategoryEntity
import de.fh.erfurt.moviemate.movies.data.local.model.LocalMovieEntity
import de.fh.erfurt.moviemate.movies.data.mapper.local.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalEntity
import de.fh.erfurt.moviemate.movies.data.remote.dto.BackupDataDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.sync.local.LocalDataLock
import de.fh.erfurt.moviemate.movies.data.sync.local.SyncDeletionTracker
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.movies.domain.sync.DataChangeNotifier
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class LocalMovieRepository(
    private val localDataSource: MovieLocalDataSource,
    private val categoryDataSource: CategoryLocalDataSource,
    private val idPolicy: LocalIdPolicy = MovieLocalIdPolicy,
    private val syncDeletionTracker: SyncDeletionTracker? = null,
    private val dataChangeNotifier: DataChangeNotifier = DataChangeNotifier(),
    private val localDataLock: LocalDataLock = LocalDataLock(),
    private val mutationTimestampProvider: (lastUpdatedEpochMillis: Long) -> Long = { lastUpdated ->
        lastUpdated.nextTimestamp()
    },
) : MovieRepository {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override suspend fun getMovies(): List<Movie> {
        return localDataSource.getMovies().map { entity -> entity.toDomainModel() }
    }

    override suspend fun getMovie(movieId: String): Movie? {
        return localDataSource.getMovie(movieId)?.toDomainModel()
    }

    override suspend fun addMovie(draft: MovieDraft): Movie {
        val storedMovie = localDataLock.withLock {
            val existingIds = localDataSource.getMovies().map { it.id }
            localDataSource.insertMovie(
                draft.toLocalEntity(idPolicy.nextId(existingIds)).copy(version = InitialLocalVersion),
            )
        }
        dataChangeNotifier.notifyDataChanged()
        return storedMovie.toDomainModel()
    }

    override suspend fun updateMovie(movie: Movie): Movie {
        val result = localDataLock.withLock {
            val existing = localDataSource.getMovie(movie.id)
            val requested = movie.toLocalEntity()
            if (existing == null) {
                throw NoSuchElementException("Movie not found: ${movie.id}")
            } else {
                val contentChanged = requested.copy(
                    updatedAtEpochMillis = existing.updatedAtEpochMillis,
                    version = existing.version,
                ) != existing
                if (!contentChanged) {
                    existing to false
                } else {
                    val resolvedTimestamp = if (requested.updatedAtEpochMillis > existing.updatedAtEpochMillis) {
                        requested.updatedAtEpochMillis
                    } else {
                        mutationTimestampProvider(existing.updatedAtEpochMillis)
                            .coerceAtLeast(existing.updatedAtEpochMillis.nextTimestamp())
                    }
                    val resolvedVersion = maxOf(
                        requested.version ?: 0L,
                        existing.version ?: 0L,
                    ).nextVersion()
                    localDataSource.updateMovie(
                        requested.copy(
                            updatedAtEpochMillis = resolvedTimestamp,
                            version = resolvedVersion,
                        ),
                    ) to true
                }
            }
        }
        if (result.second) dataChangeNotifier.notifyDataChanged()
        return result.first.toDomainModel()
    }

    override suspend fun deleteMovie(movieId: String) {
        val deleted = localDataLock.withLock {
            val existing = localDataSource.getMovie(movieId) ?: return@withLock false
            val deletedAt = mutationTimestampProvider(existing.updatedAtEpochMillis)
                .coerceAtLeast(existing.updatedAtEpochMillis.nextTimestamp())
            val deletionTracker = syncDeletionTracker
            if (deletionTracker == null) {
                localDataSource.deleteMovie(movieId)
            } else {
                deletionTracker.deleteMovieWithTombstone(movieId, deletedAt)
            }
            true
        }
        if (deleted) dataChangeNotifier.notifyDataChanged()
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
        val movie = getMovie(movieId) ?: return
        updateMovie(
            movie.copy(
                isWatched = isWatched,
                updatedAtEpochMillis = updatedAtEpochMillis,
            ),
        )
    }

    suspend fun updateMovieUserRating(movieId: String, userRating: Int?, updatedAtEpochMillis: Long) {
        val movie = getMovie(movieId) ?: return
        updateMovie(
            movie.copy(
                userRating = userRating,
                updatedAtEpochMillis = updatedAtEpochMillis,
            ),
        )
    }

    override suspend fun exportBackup(): String {
        val moviesDto = getMovies().map { movie ->
            MovieDto(
                id = movie.id,
                title = movie.title,
                description = movie.description,
                releaseYear = movie.releaseYear,
                categoryId = movie.categoryId,
                posterUrl = movie.posterUrl,
                runtimeMinutes = movie.runtimeMinutes,
                isWatched = movie.isWatched,
                comment = movie.comment,
                userRating = movie.userRating,
                imdbRating = movie.imdbRating,
                externalIds = movie.externalIds,
                watchProviders = movie.watchProviders,
                updatedAtEpochMillis = movie.updatedAtEpochMillis,
                version = movie.version,
            )
        }

        val categoriesDto = categoryDataSource.getCategories().map { category ->
            CategoryDto(
                id = category.id,
                name = category.name,
                iconKey = category.iconKey,
                updatedAtEpochMillis = category.updatedAtEpochMillis,
                version = category.version,
            )
        }

        val backup = BackupDataDto(movies = moviesDto, categories = categoriesDto)
        return jsonSerializer.encodeToString(BackupDataDto.serializer(), backup)
    }

    override suspend fun importBackup(jsonString: String) {
        val backup = jsonSerializer.decodeFromString(BackupDataDto.serializer(), jsonString)
        localDataLock.withLock {
            backup.categories.forEach { dto ->
                val entity = LocalCategoryEntity(
                    id = dto.id,
                    name = dto.name,
                    iconKey = dto.iconKey,
                    updatedAtEpochMillis = dto.updatedAtEpochMillis,
                    version = dto.version,
                )
                if (categoryDataSource.getCategory(entity.id) != null) {
                    categoryDataSource.updateCategory(entity)
                } else {
                    categoryDataSource.insertCategory(entity)
                }
            }

            backup.movies.forEach { dto ->
                val entity = LocalMovieEntity(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description,
                    releaseYear = dto.releaseYear?.toLong(),
                    categoryId = dto.categoryId,
                    posterUrl = dto.posterUrl,
                    runtimeMinutes = dto.runtimeMinutes?.toLong(),
                    isWatched = if (dto.isWatched) 1L else 0L,
                    comment = dto.comment,
                    userRating = dto.userRating?.toLong(),
                    imdbRating = dto.imdbRating,
                    externalIds = dto.externalIds?.let {
                        jsonSerializer.encodeToString(MapSerializer(String.serializer(), String.serializer()), it)
                    },
                    watchProviders = dto.watchProviders?.let {
                        jsonSerializer.encodeToString(ListSerializer(String.serializer()), it)
                    },
                    updatedAtEpochMillis = dto.updatedAtEpochMillis,
                    version = dto.version,
                )
                if (localDataSource.getMovie(entity.id) != null) {
                    localDataSource.updateMovie(entity)
                } else {
                    localDataSource.insertMovie(entity)
                }
            }
        }
        dataChangeNotifier.notifyDataChanged()
    }
}

private fun Long.nextTimestamp(): Long = if (this == Long.MAX_VALUE) this else this + 1L

private fun Long.nextVersion(): Long = if (this == Long.MAX_VALUE) this else this + 1L

private const val InitialLocalVersion = 1L
