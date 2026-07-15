package de.fh.erfurt.moviemate.platform

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.app.di.SharedModuleFactory
import de.fh.erfurt.moviemate.config.Secrets
import de.fh.erfurt.moviemate.movies.data.local.datasource.SqlDelightCategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.datasource.SqlDelightMovieLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase
import de.fh.erfurt.moviemate.movies.data.repository.local.LocalCategoryRepository
import de.fh.erfurt.moviemate.movies.data.repository.local.LocalMovieRepository
import de.fh.erfurt.moviemate.movies.data.repository.sync.GoogleDriveSyncRepository
import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesHttpClientFactory
import de.fh.erfurt.moviemate.movies.data.remote.datasource.KtorOmdbRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.repository.remote.OmdbMovieMetadataRepository
import de.fh.erfurt.moviemate.movies.data.sync.local.LocalDataLock
import de.fh.erfurt.moviemate.movies.data.sync.local.SqlDelightSyncLocalDataSource
import de.fh.erfurt.moviemate.movies.data.sync.remote.KtorGoogleDriveSyncDataSource
import de.fh.erfurt.moviemate.movies.domain.sync.DataChangeNotifier
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

class IosAppContainerFactory {
    fun createApi(): IosMovieMateApi = IosMovieMateApi(create())

    fun create(): AppContainer {

        // Movies configuration (shared HttpClient from MoviesHttpClientFactory)
        /*val moviesApiConfiguration = MoviesApiConfiguration(
            rawBaseUrl = "https://example.invalid/api",
        )
        val moviesHttpClientFactory = MoviesHttpClientFactory()
        val moviesHttpClient = moviesHttpClientFactory.create(Darwin)
        val movieRemoteDataSource = KtorMovieRemoteDataSource(
            httpClient = moviesHttpClient,
            apiConfiguration = moviesApiConfiguration,
        )
        val movieRepository = RemoteMovieRepository(movieRemoteDataSource)

        // Categories configuration
        val categoryRemoteDataSource = KtorCategoryRemoteDataSource(
            httpClient = moviesHttpClient,
            apiConfiguration = moviesApiConfiguration,
        )
        val categoryRepository = RemoteCategoryRepository(categoryRemoteDataSource)

        return SharedModuleFactory.createAppContainer(
            movieRepository = movieRepository,
            categoryRepository = categoryRepository,
        )
        */
        val driver = NativeSqliteDriver(
            schema = MovieMateDatabase.Schema,
            name = "movie-mate.db",
        )
        val database = MovieMateDatabase(driver)
        val localMovieDataSource = SqlDelightMovieLocalDataSource(database)
        val localCategoryDataSource = SqlDelightCategoryLocalDataSource(database)
        val syncLocalDataSource = SqlDelightSyncLocalDataSource(database)
        val localDataLock = LocalDataLock()
        val dataChangeNotifier = DataChangeNotifier()
        val mutationTimestampProvider: (Long) -> Long = { lastUpdated ->
            val currentEpochMillis = (NSDate().timeIntervalSince1970() * MillisPerSecond).toLong()
            maxOf(currentEpochMillis, lastUpdated.nextTimestamp())
        }
        val movieRepository = LocalMovieRepository(
            localDataSource = localMovieDataSource,
            categoryDataSource = localCategoryDataSource,
            syncDeletionTracker = syncLocalDataSource,
            dataChangeNotifier = dataChangeNotifier,
            localDataLock = localDataLock,
            mutationTimestampProvider = mutationTimestampProvider,
        )
        val categoryRepository = LocalCategoryRepository(
            localDataSource = localCategoryDataSource,
            syncDeletionTracker = syncLocalDataSource,
            dataChangeNotifier = dataChangeNotifier,
            localDataLock = localDataLock,
            mutationTimestampProvider = mutationTimestampProvider,
        )
        val sharedHttpClient = MoviesHttpClientFactory().create(Darwin)
        val omdbRemoteDataSource = KtorOmdbRemoteDataSource(
            httpClient = sharedHttpClient,
            apiKey = Secrets.OMDB_API_KEY,
        )
        val movieMetadataRepository = OmdbMovieMetadataRepository(
            omdbRemoteDataSource = omdbRemoteDataSource,
        )
        val cloudSyncRepository = GoogleDriveSyncRepository(
            localDataSource = syncLocalDataSource,
            remoteDataSource = KtorGoogleDriveSyncDataSource(sharedHttpClient),
            localDataLock = localDataLock,
            dataChangeNotifier = dataChangeNotifier,
        )

        return SharedModuleFactory.createAppContainer(
            movieRepository = movieRepository,
            categoryRepository = categoryRepository,
            movieMetadataRepository = movieMetadataRepository,
            cloudSyncRepository = cloudSyncRepository,
            dataChangeNotifier = dataChangeNotifier,
        )
    }
}

private fun Long.nextTimestamp(): Long = if (this == Long.MAX_VALUE) this else this + 1L

private const val MillisPerSecond = 1_000.0
