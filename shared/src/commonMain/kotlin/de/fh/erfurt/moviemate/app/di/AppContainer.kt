package de.fh.erfurt.moviemate.app.di

import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.DeleteMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesByCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesSortedUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.SearchMoviesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieWatchedStatusUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUserRatingUseCase

class AppContainer(
    movieRepository: MovieRepository,
    categoryRepository: CategoryRepository
) {
    // Movies layer
    val getMoviesUseCase = GetMoviesUseCase(movieRepository)
    val getMovieUseCase = GetMovieUseCase(movieRepository)
    val addMovieUseCase = AddMovieUseCase(movieRepository)
    val updateMovieUseCase = UpdateMovieUseCase(movieRepository)
    val deleteMovieUseCase = DeleteMovieUseCase(movieRepository)
    val searchMoviesUseCase = SearchMoviesUseCase(movieRepository)
    val getMoviesByCategoryUseCase = GetMoviesByCategoryUseCase(movieRepository)
    val getMoviesSortedUseCase = GetMoviesSortedUseCase(movieRepository)
    val updateMovieWatchedStatusUseCase = UpdateMovieWatchedStatusUseCase(movieRepository)
    val updateMovieUserRatingUseCase = UpdateMovieUserRatingUseCase(movieRepository)

    //val moviesListViewModel = MoviesListViewModel(getMoviesUseCase)
    //val movieDetailViewModel = MovieDetailViewModel(getMoviesUseCase)
    //val movieEditViewModel = MovieEditViewModel(addMovieUseCase)

    // Categories layer
    val getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)
    val getCategoryUseCase = GetCategoryUseCase(categoryRepository)
}
