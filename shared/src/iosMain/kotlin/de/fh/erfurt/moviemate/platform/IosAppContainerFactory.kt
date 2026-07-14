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
import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesHttpClientFactory
import de.fh.erfurt.moviemate.movies.data.remote.datasource.KtorOmdbRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.repository.remote.OmdbMovieMetadataRepository
import io.ktor.client.engine.darwin.Darwin

class IosAppContainerFactory {
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
        val movieRepository = LocalMovieRepository(
            localDataSource = localMovieDataSource,
            categoryDataSource = localCategoryDataSource,
        )
        val categoryRepository = LocalCategoryRepository(localCategoryDataSource)
        val omdbHttpClient = MoviesHttpClientFactory().create(Darwin)
        val omdbRemoteDataSource = KtorOmdbRemoteDataSource(
            httpClient = omdbHttpClient,
            apiKey = Secrets.OMDB_API_KEY,
        )
        val movieMetadataRepository = OmdbMovieMetadataRepository(
            omdbRemoteDataSource = omdbRemoteDataSource,
        )

        return SharedModuleFactory.createAppContainer(
            movieRepository = movieRepository,
            categoryRepository = categoryRepository,
            movieMetadataRepository = movieMetadataRepository,
        )
    }
}
