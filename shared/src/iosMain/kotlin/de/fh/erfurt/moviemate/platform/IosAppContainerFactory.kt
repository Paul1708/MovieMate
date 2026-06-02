package de.fh.erfurt.moviemate.platform

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.app.di.SharedModuleFactory
import de.fh.erfurt.moviemate.movies.data.local.datasource.SqlDelightCategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.datasource.SqlDelightMovieLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase
import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase.Companion.invoke
import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesApiConfiguration
import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesHttpClientFactory
import de.fh.erfurt.moviemate.movies.data.remote.datasource.KtorCategoryRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.remote.datasource.KtorMovieRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.repository.local.LocalCategoryRepository
import de.fh.erfurt.moviemate.movies.data.repository.local.LocalMovieRepository
import de.fh.erfurt.moviemate.movies.data.repository.remote.RemoteCategoryRepository
import de.fh.erfurt.moviemate.movies.data.repository.remote.RemoteMovieRepository
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
        val movieRepository = LocalMovieRepository(localMovieDataSource)
        val categoryRepository = LocalCategoryRepository(localCategoryDataSource)

        return SharedModuleFactory.createAppContainer(
            movieRepository = movieRepository,
            categoryRepository = categoryRepository,
        )
    }
}