package de.fh.erfurt.moviemate.movies.domain.sync

interface CloudSyncRepository {
    suspend fun sync(accessToken: String, accountId: String): SyncResult
}
