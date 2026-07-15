package de.fh.erfurt.moviemate.movies.data.sync.remote

data class RemoteSyncSnapshotFile(
    val id: String,
    val name: String,
    val version: Long?,
    val content: String,
)

interface GoogleDriveSyncDataSource {
    suspend fun getCurrentAccountId(accessToken: String): String

    suspend fun listSnapshots(accessToken: String): List<RemoteSyncSnapshotFile>

    suspend fun writeSnapshot(
        accessToken: String,
        fileName: String,
        existingFileId: String?,
        content: String,
    )
}

const val SyncSnapshotFilePrefix = "moviemate-sync-v1-"
const val SyncSnapshotFileSuffix = ".json"
