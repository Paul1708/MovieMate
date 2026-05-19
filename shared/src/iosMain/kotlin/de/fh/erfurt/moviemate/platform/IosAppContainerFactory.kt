package de.fh.erfurt.moviemate.platform

import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.app.di.SharedModuleFactory
import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesApiConfiguration
import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesHttpClientFactory
import de.fh.erfurt.moviemate.movies.data.remote.datasource.KtorCategoryRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.remote.datasource.KtorMovieRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.repository.remote.RemoteCategoryRepository
import de.fh.erfurt.moviemate.movies.data.repository.remote.RemoteMovieRepository
import io.ktor.client.engine.darwin.Darwin

class IosAppContainerFactory {
    fun create(): AppContainer {

        // Movies configuration (shared HttpClient from MoviesHttpClientFactory)
        val moviesApiConfiguration = MoviesApiConfiguration(
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
    }
}