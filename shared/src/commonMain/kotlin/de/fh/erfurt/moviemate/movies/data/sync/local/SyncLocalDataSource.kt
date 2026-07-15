package de.fh.erfurt.moviemate.movies.data.sync.local

import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncSnapshotDto

interface SyncDeletionTracker {
    suspend fun deleteMovieWithTombstone(movieId: String, deletedAtEpochMillis: Long)

    suspend fun deleteCategoryWithTombstone(categoryId: String, deletedAtEpochMillis: Long)
}

interface SyncLocalDataSource : SyncDeletionTracker {
    suspend fun getOrCreateDeviceId(): String

    suspend fun getBoundAccountId(): String?

    suspend fun bindAccount(accountId: String)

    suspend fun readSnapshot(deviceId: String, accountId: String): SyncSnapshotDto

    suspend fun applySnapshot(snapshot: SyncSnapshotDto): Boolean
}
