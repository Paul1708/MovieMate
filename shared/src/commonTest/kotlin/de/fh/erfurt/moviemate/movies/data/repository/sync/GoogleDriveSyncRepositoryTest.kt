package de.fh.erfurt.moviemate.movies.data.repository.sync

import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncSnapshotDto
import de.fh.erfurt.moviemate.movies.data.sync.local.LocalDataLock
import de.fh.erfurt.moviemate.movies.data.sync.local.SyncLocalDataSource
import de.fh.erfurt.moviemate.movies.data.sync.remote.GoogleDriveSyncDataSource
import de.fh.erfurt.moviemate.movies.data.sync.remote.RemoteSyncSnapshotFile
import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncException
import de.fh.erfurt.moviemate.movies.domain.sync.DataChangeNotifier
import de.fh.erfurt.moviemate.movies.domain.sync.SyncAccountMismatchException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GoogleDriveSyncRepositoryTest {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    @Test
    fun `sync merges remote data and writes only this device snapshot`() = runTest {
        val local = FakeSyncLocalDataSource(
            snapshot = snapshot(
                deviceId = DeviceId,
                movies = listOf(movie(title = "Old", updatedAt = 10)),
            ),
        )
        val remote = FakeGoogleDriveSyncDataSource(
            files = listOf(
                remoteFile(
                    id = "remote-file",
                    name = "moviemate-sync-v1-other-device.json",
                    snapshot = snapshot(
                        deviceId = "other-device",
                        movies = listOf(movie(title = "New", updatedAt = 20)),
                    ),
                ),
            ),
        )
        val notifier = DataChangeNotifier()
        val repository = repository(local, remote, notifier)

        val result = repository.sync(AccessToken, AccountId)

        assertEquals("New", local.snapshot.movies.single().title)
        assertEquals("moviemate-sync-v1-$DeviceId.json", remote.lastWrite?.fileName)
        assertEquals(null, remote.lastWrite?.existingFileId)
        assertEquals(1, result.remoteSnapshotCount)
        assertTrue(result.localDataChanged)
        assertEquals(1L, notifier.revision.value)
    }

    @Test
    fun `empty metadata-only own file is ignored then reused`() = runTest {
        val local = FakeSyncLocalDataSource(
            snapshot = snapshot(
                deviceId = DeviceId,
                movies = listOf(movie(title = "Local", updatedAt = 10)),
            ),
        )
        val remote = FakeGoogleDriveSyncDataSource(
            files = listOf(
                RemoteSyncSnapshotFile(
                    id = "empty-own-file",
                    name = "moviemate-sync-v1-$DeviceId.json",
                    version = 2,
                    content = "  ",
                ),
            ),
        )

        val result = repository(local, remote).sync(AccessToken, AccountId)

        assertEquals(0, result.remoteSnapshotCount)
        assertEquals("empty-own-file", remote.lastWrite?.existingFileId)
        val uploaded = json.decodeFromString<SyncSnapshotDto>(remote.lastWrite!!.content)
        assertEquals("Local", uploaded.movies.single().title)
    }

    @Test
    fun `non-empty corrupt snapshot fails without upload`() = runTest {
        val local = FakeSyncLocalDataSource(snapshot = snapshot(deviceId = DeviceId))
        val remote = FakeGoogleDriveSyncDataSource(
            files = listOf(
                RemoteSyncSnapshotFile(
                    id = "broken",
                    name = "moviemate-sync-v1-other.json",
                    version = 1,
                    content = "{not-json",
                ),
            ),
        )

        assertFailsWith<CloudSyncException> {
            repository(local, remote).sync(AccessToken, AccountId)
        }
        assertEquals(null, remote.lastWrite)
    }

    @Test
    fun `different account is rejected before snapshot access`() = runTest {
        val local = FakeSyncLocalDataSource(
            snapshot = snapshot(deviceId = DeviceId),
            boundAccountId = "another-account",
        )
        val remote = FakeGoogleDriveSyncDataSource()

        assertFailsWith<SyncAccountMismatchException> {
            repository(local, remote).sync(AccessToken, AccountId)
        }
        assertTrue(remote.wasAccountRead)
        assertFalse(remote.wasListed)
    }

    @Test
    fun `Drive account id is canonical across platform identifiers`() = runTest {
        val local = FakeSyncLocalDataSource(snapshot = snapshot(deviceId = DeviceId))
        val remote = FakeGoogleDriveSyncDataSource()

        repository(local, remote).sync(AccessToken, "platform-specific-id")

        assertEquals(AccountId, local.boundAccountId)
        val uploaded = json.decodeFromString<SyncSnapshotDto>(remote.lastWrite!!.content)
        assertEquals(AccountId, uploaded.accountId)
    }

    private fun repository(
        local: FakeSyncLocalDataSource,
        remote: FakeGoogleDriveSyncDataSource,
        notifier: DataChangeNotifier = DataChangeNotifier(),
    ): GoogleDriveSyncRepository = GoogleDriveSyncRepository(
        localDataSource = local,
        remoteDataSource = remote,
        localDataLock = LocalDataLock(),
        dataChangeNotifier = notifier,
        json = json,
    )

    private fun remoteFile(
        id: String,
        name: String,
        snapshot: SyncSnapshotDto,
    ): RemoteSyncSnapshotFile = RemoteSyncSnapshotFile(
        id = id,
        name = name,
        version = 1,
        content = json.encodeToString(snapshot),
    )
}

private class FakeSyncLocalDataSource(
    var snapshot: SyncSnapshotDto,
    var boundAccountId: String? = null,
) : SyncLocalDataSource {
    override suspend fun getOrCreateDeviceId(): String = snapshot.deviceId

    override suspend fun getBoundAccountId(): String? = boundAccountId

    override suspend fun bindAccount(accountId: String) {
        val current = boundAccountId
        if (current != null && current != accountId) throw SyncAccountMismatchException()
        boundAccountId = accountId
    }

    override suspend fun readSnapshot(deviceId: String, accountId: String): SyncSnapshotDto {
        return snapshot.copy(deviceId = deviceId, accountId = accountId)
    }

    override suspend fun applySnapshot(snapshot: SyncSnapshotDto): Boolean {
        val dataChanged = this.snapshot.movies != snapshot.movies ||
            this.snapshot.categories != snapshot.categories
        this.snapshot = snapshot
        return dataChanged
    }

    override suspend fun deleteMovieWithTombstone(movieId: String, deletedAtEpochMillis: Long) = Unit

    override suspend fun deleteCategoryWithTombstone(categoryId: String, deletedAtEpochMillis: Long) = Unit
}

private class FakeGoogleDriveSyncDataSource(
    private val files: List<RemoteSyncSnapshotFile> = emptyList(),
    private val currentAccountId: String = AccountId,
) : GoogleDriveSyncDataSource {
    var wasAccountRead = false
    var wasListed = false
    var lastWrite: Write? = null

    override suspend fun getCurrentAccountId(accessToken: String): String {
        wasAccountRead = true
        return currentAccountId
    }

    override suspend fun listSnapshots(accessToken: String): List<RemoteSyncSnapshotFile> {
        wasListed = true
        return files
    }

    override suspend fun writeSnapshot(
        accessToken: String,
        fileName: String,
        existingFileId: String?,
        content: String,
    ) {
        lastWrite = Write(fileName, existingFileId, content)
    }

    data class Write(
        val fileName: String,
        val existingFileId: String?,
        val content: String,
    )
}

private fun snapshot(
    deviceId: String,
    movies: List<MovieDto> = emptyList(),
): SyncSnapshotDto = SyncSnapshotDto(
    deviceId = deviceId,
    accountId = AccountId,
    movies = movies,
)

private fun movie(
    title: String,
    updatedAt: Long,
): MovieDto = MovieDto(
    id = "movie-1",
    title = title,
    description = "Description",
    updatedAtEpochMillis = updatedAt,
    version = 1,
)

private const val DeviceId = "device-a"
private const val AccountId = "google-account"
private const val AccessToken = "access-token"
