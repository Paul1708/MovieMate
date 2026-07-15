package de.fh.erfurt.moviemate.app.di

import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository
import de.fh.erfurt.moviemate.movies.domain.sync.CloudSyncRepository
import de.fh.erfurt.moviemate.movies.domain.sync.DataChangeNotifier

object SharedModuleFactory {
    fun createAppContainer(
        movieRepository: MovieRepository,
        categoryRepository: CategoryRepository,
        movieMetadataRepository: MovieMetadataRepository,
        cloudSyncRepository: CloudSyncRepository,
        dataChangeNotifier: DataChangeNotifier,
    ): AppContainer = AppContainer(
        movieRepository = movieRepository,
        categoryRepository = categoryRepository,
        movieMetadataRepository = movieMetadataRepository,
        cloudSyncRepository = cloudSyncRepository,
        dataChangeNotifier = dataChangeNotifier,
    )
}
