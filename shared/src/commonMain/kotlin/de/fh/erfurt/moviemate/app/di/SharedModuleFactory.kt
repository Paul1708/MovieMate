package de.fh.erfurt.moviemate.app.di

import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository

object SharedModuleFactory {
    fun createAppContainer(
        movieRepository: MovieRepository,
        categoryRepository: CategoryRepository
    ): AppContainer = AppContainer(movieRepository, categoryRepository)
}