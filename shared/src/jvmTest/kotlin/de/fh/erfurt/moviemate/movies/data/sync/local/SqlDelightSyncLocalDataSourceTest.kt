package de.fh.erfurt.moviemate.movies.data.sync.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.MovieDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncSnapshotDto
import de.fh.erfurt.moviemate.movies.data.sync.dto.SyncTombstoneDto
import de.fh.erfurt.moviemate.movies.domain.sync.SyncAccountMismatchException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SqlDelightSyncLocalDataSourceTest {
    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: MovieMateDatabase
    private lateinit var dataSource: SqlDelightSyncLocalDataSource

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        MovieMateDatabase.Schema.create(driver)
        database = MovieMateDatabase(driver)
        dataSource = SqlDelightSyncLocalDataSource(
            database = database,
            deviceIdGenerator = { "device-test" },
        )
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun `category deletion is atomic and persists tombstone`() = runTest {
        insertCategory("category-old")
        insertMovie(id = "movie-old", categoryId = "category-old")

        dataSource.deleteCategoryWithTombstone(
            categoryId = "category-old",
            deletedAtEpochMillis = 100,
        )

        val snapshot = dataSource.readSnapshot("device-test", "account")
        assertEquals(emptyList(), snapshot.categories)
        assertNull(snapshot.movies.single().categoryId)
        assertEquals(
            SyncTombstoneDto("category-old", 100),
            snapshot.deletedCategories.single(),
        )
    }

    @Test
    fun `apply snapshot deletes movies before replacing categories and inserts merged data`() = runTest {
        insertCategory("category-old")
        insertMovie(id = "movie-old", categoryId = "category-old")
        val merged = SyncSnapshotDto(
            deviceId = "device-test",
            accountId = "account",
            categories = listOf(
                CategoryDto(
                    id = "category-new",
                    name = "New",
                    updatedAtEpochMillis = 200,
                    version = 1,
                ),
            ),
            movies = listOf(
                MovieDto(
                    id = "movie-new",
                    title = "New",
                    description = "Description",
                    categoryId = "category-new",
                    updatedAtEpochMillis = 200,
                    version = 1,
                ),
            ),
            deletedMovies = listOf(SyncTombstoneDto("movie-old", 150)),
            deletedCategories = listOf(SyncTombstoneDto("category-old", 150)),
        )

        assertTrue(dataSource.applySnapshot(merged))

        val stored = dataSource.readSnapshot("device-test", "account")
        assertEquals(listOf("movie-new"), stored.movies.map(MovieDto::id))
        assertEquals("category-new", stored.movies.single().categoryId)
        assertEquals(listOf("category-new"), stored.categories.map(CategoryDto::id))
        assertEquals("movie-old", stored.deletedMovies.single().id)
        assertEquals("category-old", stored.deletedCategories.single().id)
    }

    @Test
    fun `device id persists and account cannot silently change`() = runTest {
        assertEquals("device-test", dataSource.getOrCreateDeviceId())
        assertEquals("device-test", dataSource.getOrCreateDeviceId())

        dataSource.bindAccount("account-a")
        assertEquals("account-a", dataSource.getBoundAccountId())
        assertFailsWith<SyncAccountMismatchException> {
            dataSource.bindAccount("account-b")
        }
    }

    private fun insertCategory(id: String) {
        database.categoryEntityQueries.insertCategory(
            id = id,
            name = id,
            iconKey = null,
            updatedAtEpochMillis = 10,
            version = 1,
        )
    }

    private fun insertMovie(id: String, categoryId: String?) {
        database.movieEntityQueries.insertMovie(
            id = id,
            title = id,
            description = "Description",
            releaseYear = null,
            categoryId = categoryId,
            posterUrl = null,
            runtimeMinutes = null,
            isWatched = 0,
            comment = null,
            userRating = null,
            imdbRating = null,
            externalIds = null,
            watchProviders = null,
            updatedAtEpochMillis = 10,
            version = 1,
        )
    }
}
