package de.fh.erfurt.moviemate.movies.data.sync.local

import de.fh.erfurt.moviemate.movies.data.local.db.CategoryEntity
import de.fh.erfurt.moviemate.movies.data.local.db.MovieEntity
import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase
import de.fh.erfurt.moviemate.movies.data.mapper.local.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalModel
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncSnapshotDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncTombstoneDto
import de.fh.erfurt.moviemate.movies.domain.sync.SyncAccountMismatchException
import kotlin.random.Random
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class SqlDelightSyncLocalDataSource(
    private val database: MovieMateDatabase,
    private val json: Json = defaultSyncJson(),
    private val deviceIdGenerator: () -> String = ::generateDeviceId,
) : SyncLocalDataSource {
    private val syncQueries = database.syncStateQueries
    private val movieQueries = database.movieEntityQueries
    private val categoryQueries = database.categoryEntityQueries

    override suspend fun getOrCreateDeviceId(): String {
        return database.transactionWithResult {
            syncQueries.selectMetadata(DeviceIdMetadataKey).executeAsOneOrNull()
                ?: deviceIdGenerator().also { generatedId ->
                    require(generatedId.isNotBlank()) { "Generated sync device id must not be blank." }
                    syncQueries.upsertMetadata(DeviceIdMetadataKey, generatedId)
                }
        }
    }

    override suspend fun getBoundAccountId(): String? {
        return syncQueries.selectMetadata(BoundAccountMetadataKey).executeAsOneOrNull()
    }

    override suspend fun bindAccount(accountId: String) {
        require(accountId.isNotBlank()) { "Sync account id must not be blank." }
        database.transaction {
            val boundAccountId = syncQueries
                .selectMetadata(BoundAccountMetadataKey)
                .executeAsOneOrNull()
            when {
                boundAccountId == null -> syncQueries.upsertMetadata(BoundAccountMetadataKey, accountId)
                boundAccountId != accountId -> throw SyncAccountMismatchException()
            }
        }
    }

    override suspend fun readSnapshot(
        deviceId: String,
        accountId: String,
    ): SyncSnapshotDto {
        return database.transactionWithResult {
            readSnapshotInTransaction(deviceId = deviceId, accountId = accountId)
        }
    }

    override suspend fun applySnapshot(snapshot: SyncSnapshotDto): Boolean {
        return database.transactionWithResult {
            val moviesBefore = movieQueries.selectAll().executeAsList().map { movie -> movie.toSyncDto() }
            val categoriesBefore = categoryQueries.selectAll().executeAsList().map { category ->
                category.toSyncDto()
            }

            snapshot.deletedMovies.forEach { tombstone ->
                upsertTombstone(MovieEntityType, tombstone)
            }
            snapshot.deletedCategories.forEach { tombstone ->
                upsertTombstone(CategoryEntityType, tombstone)
            }

            val liveMovieIds = snapshot.movies.mapTo(mutableSetOf(), MovieDto::id)
            snapshot.deletedMovies.forEach { tombstone ->
                if (tombstone.id !in liveMovieIds) {
                    movieQueries.deleteMovie(tombstone.id)
                }
            }

            val liveCategoryIds = snapshot.categories.mapTo(mutableSetOf(), CategoryDto::id)
            snapshot.deletedCategories.forEach { tombstone ->
                if (tombstone.id !in liveCategoryIds) {
                    movieQueries.clearCategoryFromMovies(tombstone.id)
                    categoryQueries.deleteCategory(tombstone.id)
                }
            }

            snapshot.categories.forEach(::upsertCategory)
            snapshot.movies.forEach { movie ->
                upsertMovie(
                    if (movie.categoryId == null || movie.categoryId in liveCategoryIds) {
                        movie
                    } else {
                        movie.copy(categoryId = null)
                    },
                )
            }

            val moviesAfter = movieQueries.selectAll().executeAsList().map { movie -> movie.toSyncDto() }
            val categoriesAfter = categoryQueries.selectAll().executeAsList().map { category ->
                category.toSyncDto()
            }
            moviesBefore != moviesAfter || categoriesBefore != categoriesAfter
        }
    }

    override suspend fun deleteMovieWithTombstone(
        movieId: String,
        deletedAtEpochMillis: Long,
    ) {
        require(movieId.isNotBlank()) { "Movie id must not be blank." }
        database.transaction {
            upsertTombstone(
                entityType = MovieEntityType,
                tombstone = SyncTombstoneDto(movieId, deletedAtEpochMillis),
            )
            movieQueries.deleteMovie(movieId)
        }
    }

    override suspend fun deleteCategoryWithTombstone(
        categoryId: String,
        deletedAtEpochMillis: Long,
    ) {
        require(categoryId.isNotBlank()) { "Category id must not be blank." }
        database.transaction {
            upsertTombstone(
                entityType = CategoryEntityType,
                tombstone = SyncTombstoneDto(categoryId, deletedAtEpochMillis),
            )
            movieQueries.clearCategoryFromMovies(categoryId)
            categoryQueries.deleteCategory(categoryId)
        }
    }

    private fun readSnapshotInTransaction(
        deviceId: String,
        accountId: String,
    ): SyncSnapshotDto {
        return SyncSnapshotDto(
            deviceId = deviceId,
            accountId = accountId,
            movies = movieQueries.selectAll().executeAsList().map { movie -> movie.toSyncDto() },
            categories = categoryQueries.selectAll().executeAsList().map { category ->
                category.toSyncDto()
            },
            deletedMovies = readTombstones(MovieEntityType),
            deletedCategories = readTombstones(CategoryEntityType),
        )
    }

    private fun readTombstones(entityType: String): List<SyncTombstoneDto> {
        return syncQueries.selectTombstonesByType(entityType).executeAsList().map { tombstone ->
            SyncTombstoneDto(
                id = tombstone.entityId,
                deletedAtEpochMillis = tombstone.deletedAtEpochMillis,
            )
        }
    }

    private fun upsertTombstone(
        entityType: String,
        tombstone: SyncTombstoneDto,
    ) {
        val existing = syncQueries
            .selectTombstone(entityType, tombstone.id)
            .executeAsOneOrNull()
        if (existing == null || tombstone.deletedAtEpochMillis > existing.deletedAtEpochMillis) {
            syncQueries.upsertTombstone(
                entityType = entityType,
                entityId = tombstone.id,
                deletedAtEpochMillis = tombstone.deletedAtEpochMillis,
            )
        }
    }

    private fun upsertCategory(category: CategoryDto) {
        val existing = categoryQueries.selectById(category.id).executeAsOneOrNull()
        if (existing == null) {
            categoryQueries.insertCategory(
                id = category.id,
                name = category.name,
                iconKey = category.iconKey,
                updatedAtEpochMillis = category.updatedAtEpochMillis,
                version = category.version,
            )
        } else if (existing.toSyncDto() != category) {
            categoryQueries.updateCategory(
                name = category.name,
                iconKey = category.iconKey,
                updatedAtEpochMillis = category.updatedAtEpochMillis,
                version = category.version,
                id = category.id,
            )
        }
    }

    private fun upsertMovie(movie: MovieDto) {
        val existing = movieQueries.selectById(movie.id).executeAsOneOrNull()
        val externalIds = movie.externalIds?.let { ids ->
            json.encodeToString(MapSerializer(String.serializer(), String.serializer()), ids)
        }
        val watchProviders = movie.watchProviders?.let { providers ->
            json.encodeToString(ListSerializer(String.serializer()), providers)
        }

        if (existing == null) {
            movieQueries.insertMovie(
                id = movie.id,
                title = movie.title,
                description = movie.description,
                releaseYear = movie.releaseYear?.toLong(),
                categoryId = movie.categoryId,
                posterUrl = movie.posterUrl,
                runtimeMinutes = movie.runtimeMinutes?.toLong(),
                isWatched = if (movie.isWatched) 1L else 0L,
                comment = movie.comment,
                userRating = movie.userRating?.toLong(),
                imdbRating = movie.imdbRating,
                externalIds = externalIds,
                watchProviders = watchProviders,
                updatedAtEpochMillis = movie.updatedAtEpochMillis,
                version = movie.version,
                mediaType = movie.mediaType?.name,
                totalSeasons = movie.totalSeasons?.toLong(),
            )
        } else if (existing.toSyncDto() != movie) {
            movieQueries.updateMovie(
                title = movie.title,
                description = movie.description,
                releaseYear = movie.releaseYear?.toLong(),
                categoryId = movie.categoryId,
                posterUrl = movie.posterUrl,
                runtimeMinutes = movie.runtimeMinutes?.toLong(),
                isWatched = if (movie.isWatched) 1L else 0L,
                comment = movie.comment,
                userRating = movie.userRating?.toLong(),
                imdbRating = movie.imdbRating,
                externalIds = externalIds,
                watchProviders = watchProviders,
                updatedAtEpochMillis = movie.updatedAtEpochMillis,
                version = movie.version,
                mediaType = movie.mediaType?.name,
                totalSeasons = movie.totalSeasons?.toLong(),
                id = movie.id,
            )
        }
    }

    private fun MovieEntity.toSyncDto(): MovieDto {
        val movie = toLocalModel().toDomainModel()
        return MovieDto(
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
            mediaType = movie.mediaType.takeIf { mediaType != null },
            totalSeasons = movie.totalSeasons,
        )
    }

    private fun CategoryEntity.toSyncDto(): CategoryDto {
        val category = toLocalModel().toDomainModel()
        return CategoryDto(
            id = category.id,
            name = category.name,
            iconKey = category.iconKey,
            updatedAtEpochMillis = category.updatedAtEpochMillis,
            version = category.version,
        )
    }
}

private fun generateDeviceId(): String {
    val first = Random.nextLong().toULong().toString(radix = 16)
    val second = Random.nextLong().toULong().toString(radix = 16)
    return "device-$first-$second"
}

private fun defaultSyncJson(): Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

private const val DeviceIdMetadataKey = "sync.deviceId"
private const val BoundAccountMetadataKey = "sync.boundAccountId"
private const val MovieEntityType = "movie"
private const val CategoryEntityType = "category"
