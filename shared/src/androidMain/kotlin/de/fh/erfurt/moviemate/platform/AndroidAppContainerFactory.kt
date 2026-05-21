package de.fh.erfurt.moviemate.platform

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase
import android.content.Context
import de.fh.erfurt.moviemate.app.di.SharedModuleFactory
import de.fh.erfurt.moviemate.movies.data.local.datasource.SqlDelightCategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.datasource.SqlDelightMovieLocalDataSource
import de.fh.erfurt.moviemate.movies.data.repository.local.LocalCategoryRepository
import de.fh.erfurt.moviemate.movies.data.repository.local.LocalMovieRepository


class AndroidAppContainerFactory(
    private val context: Context,
    ) {
    fun create(): AppContainer {
        // Movies configuration (shared HttpClient from NotesHttpClientFactory)
        /*val moviesApiConfiguration = MoviesApiConfiguration(
            rawBaseUrl = "https://example.invalid/api",
        )
        val moviesHttpClientFactory = MoviesHttpClientFactory()
        val moviesHttpClient = moviesHttpClientFactory.create(OkHttp)
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
        )*/
        val driver = AndroidSqliteDriver(
            schema = MovieMateDatabase.Schema,
            context = context,
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