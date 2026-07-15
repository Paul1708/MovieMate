package de.fh.erfurt.moviemate.movies.data.sync

import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.CurrentSyncSchemaVersion
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncSnapshotDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncTombstoneDto
import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class SyncSnapshotMerger(
    private val json: Json,
) {
    fun merge(
        snapshots: List<SyncSnapshotDto>,
        targetDeviceId: String,
        accountId: String,
    ): SyncSnapshotDto {
        require(targetDeviceId.isNotBlank()) { "Sync device id must not be blank." }
        require(accountId.isNotBlank()) { "Sync account id must not be blank." }

        snapshots.forEach(::validateSnapshot)
        snapshots.firstOrNull { snapshot -> snapshot.accountId != accountId }?.let {
            throw CloudSyncException("A Google Drive snapshot belongs to another account.")
        }

        val movieTombstones = mergeTombstones(snapshots.flatMap(SyncSnapshotDto::deletedMovies))
        val categoryTombstones = mergeTombstones(snapshots.flatMap(SyncSnapshotDto::deletedCategories))

        val categories = snapshots
            .flatMap(SyncSnapshotDto::categories)
            .groupBy(CategoryDto::id)
            .mapNotNull { (id, candidates) ->
                val winner = candidates.maxWithOrNull(categoryComparator)
                    ?: return@mapNotNull null
                val deletion = categoryTombstones[id]
                winner.takeIf { deletion == null || winner.updatedAtEpochMillis > deletion.deletedAtEpochMillis }
            }
            .sortedBy(CategoryDto::id)

        val liveCategoryIds = categories.mapTo(mutableSetOf(), CategoryDto::id)
        val movies = snapshots
            .flatMap(SyncSnapshotDto::movies)
            .groupBy(MovieDto::id)
            .mapNotNull { (id, candidates) ->
                val winner = candidates.maxWithOrNull(movieComparator)
                    ?: return@mapNotNull null
                val deletion = movieTombstones[id]
                winner
                    .takeIf { deletion == null || winner.updatedAtEpochMillis > deletion.deletedAtEpochMillis }
                    ?.let { movie ->
                        if (movie.categoryId == null || movie.categoryId in liveCategoryIds) {
                            movie
                        } else {
                            movie.copy(categoryId = null)
                        }
                    }
            }
            .sortedBy(MovieDto::id)

        return SyncSnapshotDto(
            deviceId = targetDeviceId,
            accountId = accountId,
            movies = movies,
            categories = categories,
            deletedMovies = movieTombstones.values.sortedBy(SyncTombstoneDto::id),
            deletedCategories = categoryTombstones.values.sortedBy(SyncTombstoneDto::id),
        )
    }

    private fun validateSnapshot(snapshot: SyncSnapshotDto) {
        if (snapshot.schemaVersion != CurrentSyncSchemaVersion) {
            throw CloudSyncException(
                "Unsupported MovieMate sync schema ${snapshot.schemaVersion}.",
            )
        }
        if (snapshot.deviceId.isBlank() || snapshot.accountId.isBlank()) {
            throw CloudSyncException("MovieMate sync snapshot identity is incomplete.")
        }
    }

    private fun mergeTombstones(
        tombstones: List<SyncTombstoneDto>,
    ): Map<String, SyncTombstoneDto> {
        return tombstones
            .filter { tombstone -> tombstone.id.isNotBlank() }
            .groupBy(SyncTombstoneDto::id)
            .mapValues { (_, candidates) ->
                candidates.maxWithOrNull(
                    compareBy<SyncTombstoneDto> { it.deletedAtEpochMillis }
                        .thenBy { json.encodeToString(it) },
                ) ?: error("A grouped tombstone list must not be empty.")
            }
    }

    private val movieComparator = compareBy<MovieDto> { movie -> movie.updatedAtEpochMillis }
        .thenBy { movie -> movie.version ?: Long.MIN_VALUE }
        .thenBy { movie -> json.encodeToString(movie) }

    private val categoryComparator = compareBy<CategoryDto> { category -> category.updatedAtEpochMillis }
        .thenBy { category -> category.version ?: Long.MIN_VALUE }
        .thenBy { category -> json.encodeToString(category) }
}
