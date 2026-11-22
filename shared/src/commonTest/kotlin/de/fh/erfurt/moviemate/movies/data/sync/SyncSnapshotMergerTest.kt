package de.fh.erfurt.moviemate.movies.data.sync

import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncSnapshotDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncTombstoneDto
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json

class SyncSnapshotMergerTest {
    private val merger = SyncSnapshotMerger(
        Json {
            explicitNulls = false
            encodeDefaults = true
        },
    )

    @Test
    fun `latest records from all device snapshots win`() {
        val local = snapshot(
            deviceId = "local",
            movies = listOf(movie(id = "movie-1", title = "Old", updatedAt = 10)),
            categories = listOf(category(id = "category-1", name = "Old", updatedAt = 10)),
        )
        val remote = snapshot(
            deviceId = "remote",
            movies = listOf(movie(id = "movie-1", title = "New", updatedAt = 20)),
            categories = listOf(category(id = "category-1", name = "New", updatedAt = 20)),
        )

        val merged = merger.merge(
            snapshots = listOf(local, remote),
            targetDeviceId = "local",
            accountId = AccountId,
        )

        assertEquals("New", merged.movies.single().title)
        assertEquals("New", merged.categories.single().name)
    }

    @Test
    fun `legacy changes retain the most recent explicit series metadata`() {
        val entry = movie(id = "show", title = "Series", updatedAt = 10).copy(
            mediaType = MediaType.SERIES,
            totalSeasons = 3,
        )
        val snapshots = listOf(
            snapshot("new-client", movies = listOf(entry)),
            snapshot("legacy-client", movies = listOf(entry.copy(
                updatedAtEpochMillis = 30,
                isWatched = true,
                mediaType = null,
                totalSeasons = null,
            ))),
            snapshot("other-client", movies = listOf(entry.copy(updatedAtEpochMillis = 20, totalSeasons = 4))),
        )
        val merged = merger.merge(snapshots, targetDeviceId = "local", accountId = AccountId)
        assertEquals(true, merged.movies.single().isWatched)
        assertEquals(MediaType.SERIES, merged.movies.single().mediaType)
        assertEquals(4, merged.movies.single().totalSeasons)
    }

    @Test
    fun `new tombstone deletes record and equal timestamp favors deletion`() {
        val record = snapshot(
            deviceId = "local",
            movies = listOf(movie(id = "movie-1", title = "Movie", updatedAt = 20)),
        )
        val deletion = snapshot(
            deviceId = "remote",
            deletedMovies = listOf(SyncTombstoneDto("movie-1", deletedAtEpochMillis = 20)),
        )

        val merged = merger.merge(
            snapshots = listOf(record, deletion),
            targetDeviceId = "local",
            accountId = AccountId,
        )

        assertEquals(emptyList(), merged.movies)
        assertEquals("movie-1", merged.deletedMovies.single().id)
    }

    @Test
    fun `newer record survives stale persistent tombstone`() {
        val record = snapshot(
            deviceId = "local",
            movies = listOf(movie(id = "movie-1", title = "Restored", updatedAt = 30)),
        )
        val deletion = snapshot(
            deviceId = "remote",
            deletedMovies = listOf(SyncTombstoneDto("movie-1", deletedAtEpochMillis = 20)),
        )

        val merged = merger.merge(
            snapshots = listOf(record, deletion),
            targetDeviceId = "local",
            accountId = AccountId,
        )

        assertEquals("Restored", merged.movies.single().title)
        assertEquals("movie-1", merged.deletedMovies.single().id)
    }

    @Test
    fun `deleted category clears dangling movie reference`() {
        val local = snapshot(
            deviceId = "local",
            movies = listOf(
                movie(
                    id = "movie-1",
                    title = "Movie",
                    updatedAt = 30,
                    categoryId = "category-1",
                ),
            ),
            categories = listOf(category(id = "category-1", name = "Category", updatedAt = 10)),
        )
        val remote = snapshot(
            deviceId = "remote",
            deletedCategories = listOf(SyncTombstoneDto("category-1", deletedAtEpochMillis = 20)),
        )

        val merged = merger.merge(
            snapshots = listOf(local, remote),
            targetDeviceId = "local",
            accountId = AccountId,
        )

        assertEquals(emptyList(), merged.categories)
        assertNull(merged.movies.single().categoryId)
    }
}

private fun snapshot(
    deviceId: String,
    movies: List<MovieDto> = emptyList(),
    categories: List<CategoryDto> = emptyList(),
    deletedMovies: List<SyncTombstoneDto> = emptyList(),
    deletedCategories: List<SyncTombstoneDto> = emptyList(),
): SyncSnapshotDto = SyncSnapshotDto(
    deviceId = deviceId,
    accountId = AccountId,
    movies = movies,
    categories = categories,
    deletedMovies = deletedMovies,
    deletedCategories = deletedCategories,
)

private fun movie(
    id: String,
    title: String,
    updatedAt: Long,
    categoryId: String? = null,
): MovieDto = MovieDto(
    id = id,
    title = title,
    description = "Description",
    categoryId = categoryId,
    updatedAtEpochMillis = updatedAt,
    version = 1,
)

private fun category(
    id: String,
    name: String,
    updatedAt: Long,
): CategoryDto = CategoryDto(
    id = id,
    name = name,
    updatedAtEpochMillis = updatedAt,
    version = 1,
)

private const val AccountId = "google-account"
