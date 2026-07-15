package de.fh.erfurt.moviemate.movies.data.sync.dto

import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import kotlinx.serialization.Serializable

@Serializable
data class SyncSnapshotDto(
    val schemaVersion: Int = CurrentSyncSchemaVersion,
    val deviceId: String,
    val accountId: String,
    val movies: List<MovieDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val deletedMovies: List<SyncTombstoneDto> = emptyList(),
    val deletedCategories: List<SyncTombstoneDto> = emptyList(),
)

@Serializable
data class SyncTombstoneDto(
    val id: String,
    val deletedAtEpochMillis: Long,
)

const val CurrentSyncSchemaVersion = 1
