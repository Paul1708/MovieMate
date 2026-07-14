package de.fh.erfurt.moviemate.app.di

import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieMetadataRepository

object SharedModuleFactory {
    fun createAppContainer(
        movieRepository: MovieRepository,
        categoryRepository: CategoryRepository,
        movieMetadataRepository: MovieMetadataRepository,
    ): AppContainer = AppContainer(
        movieRepository = movieRepository,
        categoryRepository = categoryRepository,
        movieMetadataRepository = movieMetadataRepository,
    )
}
