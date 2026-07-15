package de.fh.erfurt.moviemate.movies.data.repository.sync

import de.fh.erfurt.moviemate.movies.data.sync.SyncSnapshotMerger
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncSnapshotDto
import de.fh.erfurt.moviemate.movies.data.sync.local.LocalDataLock
import de.fh.erfurt.moviemate.movies.data.sync.local.SyncLocalDataSource
import de.fh.erfurt.moviemate.movies.data.sync.remote.GoogleDriveSyncDataSource
import de.fh.erfurt.moviemate.movies.data.sync.remote.RemoteSyncSnapshotFile
import de.fh.erfurt.moviemate.movies.data.sync.remote.SyncSnapshotFilePrefix
import de.fh.erfurt.moviemate.movies.data.sync.remote.SyncSnapshotFileSuffix
import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncException
import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncRepository
import de.fh.erfurt.moviemate.movies.domain.sync.DataChangeNotifier
import de.fh.erfurt.moviemate.movies.domain.sync.SyncAccountMismatchException
import de.fh.erfurt.moviemate.movies.domain.sync.SyncResult
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GoogleDriveSyncRepository(
    private val localDataSource: SyncLocalDataSource,
    private val remoteDataSource: GoogleDriveSyncDataSource,
    private val localDataLock: LocalDataLock,
    private val dataChangeNotifier: DataChangeNotifier,
    private val json: Json = defaultSyncJson(),
) : CloudSyncRepository {
    private val merger = SyncSnapshotMerger(json)

    override suspend fun sync(
        accessToken: String,
        accountId: String,
    ): SyncResult {
        require(accessToken.isNotBlank()) { "Google Drive access token must not be blank." }
        require(accountId.isNotBlank()) { "Google account id must not be blank." }

        // The platform SDK account identifiers are not guaranteed to be populated in the
        // same way on Android and iOS. Drive's permission id is stable for the actual Drive
        // behind this access token, so snapshots and the local binding use that canonical id.
        val canonicalAccountId = remoteDataSource.getCurrentAccountId(accessToken)

        val deviceId = localDataLock.withLock {
            ensureAccountMatches(canonicalAccountId)
            localDataSource.getOrCreateDeviceId()
        }
        val remoteFiles = remoteDataSource.listSnapshots(accessToken)
        // Drive file creation and media upload are two calls. A process can stop between them,
        // leaving an empty metadata-only file. It is safe to ignore that file as a snapshot;
        // it is still retained in [remoteFiles] so this device reuses and fills it below.
        val remoteSnapshots = remoteFiles
            .filter { file -> file.content.isNotBlank() }
            .map(::decodeSnapshot)

        val mergeResult = localDataLock.withLock {
            ensureAccountMatches(canonicalAccountId)
            localDataSource.bindAccount(canonicalAccountId)
            val localSnapshot = localDataSource.readSnapshot(
                deviceId = deviceId,
                accountId = canonicalAccountId,
            )
            val mergedSnapshot = merger.merge(
                snapshots = remoteSnapshots + localSnapshot,
                targetDeviceId = deviceId,
                accountId = canonicalAccountId,
            )
            val localDataChanged = localDataSource.applySnapshot(mergedSnapshot)
            MergeResult(mergedSnapshot, localDataChanged)
        }

        if (mergeResult.localDataChanged) {
            dataChangeNotifier.notifyDataChanged()
        }

        val ownFileName = snapshotFileName(deviceId)
        val existingOwnFileId = remoteFiles
            .asSequence()
            .filter { file -> file.name == ownFileName }
            .maxWithOrNull(
                compareBy<RemoteSyncSnapshotFile> { file -> file.version ?: Long.MIN_VALUE }
                    .thenBy(RemoteSyncSnapshotFile::id),
            )
            ?.id
        remoteDataSource.writeSnapshot(
            accessToken = accessToken,
            fileName = ownFileName,
            existingFileId = existingOwnFileId,
            content = json.encodeToString(mergeResult.snapshot),
        )

        return SyncResult(
            movieCount = mergeResult.snapshot.movies.size,
            categoryCount = mergeResult.snapshot.categories.size,
            remoteSnapshotCount = remoteSnapshots.size,
            localDataChanged = mergeResult.localDataChanged,
        )
    }

    private suspend fun ensureAccountMatches(accountId: String) {
        val boundAccountId = localDataSource.getBoundAccountId()
        if (boundAccountId != null && boundAccountId != accountId) {
            throw SyncAccountMismatchException()
        }
    }

    private fun decodeSnapshot(file: RemoteSyncSnapshotFile): SyncSnapshotDto {
        return try {
            json.decodeFromString<SyncSnapshotDto>(file.content)
        } catch (throwable: SerializationException) {
            throw CloudSyncException(
                message = "Google Drive contains an unreadable MovieMate snapshot (${file.name}).",
                cause = throwable,
            )
        }
    }
}

private data class MergeResult(
    val snapshot: SyncSnapshotDto,
    val localDataChanged: Boolean,
)

private fun snapshotFileName(deviceId: String): String {
    return "$SyncSnapshotFilePrefix$deviceId$SyncSnapshotFileSuffix"
}

private fun defaultSyncJson(): Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}
