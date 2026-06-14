package de.fh.erfurt.moviemate.app.di

import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.repository.movie.MovieRepository
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ExportBackupUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.backup.ImportBackupUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.AddCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.DeleteCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.DeleteMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.UpdateCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesByCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesSortedUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.SearchMoviesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieWatchedStatusUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUserRatingUseCase
import de.fh.erfurt.moviemate.movies.presentation.backup.BackupViewModel
import de.fh.erfurt.moviemate.movies.presentation.detail.CategoryDetailViewModel
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailViewModel
import de.fh.erfurt.moviemate.movies.presentation.edit.CategoryEditViewModel
import de.fh.erfurt.moviemate.movies.presentation.edit.MovieEditViewModel
import de.fh.erfurt.moviemate.movies.presentation.list.CategoriesListViewModel
import de.fh.erfurt.moviemate.movies.presentation.list.MoviesListViewModel

class AppContainer(
    movieRepository: MovieRepository,
    categoryRepository: CategoryRepository
) {
    // Categories layer
    val getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)
    val getCategoryUseCase = GetCategoryUseCase(categoryRepository)
    val addCategoryUseCase = AddCategoryUseCase(categoryRepository)
    val updateCategoryUseCase = UpdateCategoryUseCase(categoryRepository)
    val deleteCategoryUseCase = DeleteCategoryUseCase(categoryRepository)

    val categoriesListViewModel = CategoriesListViewModel(
        getCategoriesUseCase = getCategoriesUseCase,
        deleteCategoryUseCase = deleteCategoryUseCase,
    )
    val categoryDetailViewModel = CategoryDetailViewModel(
        getCategoryUseCase = getCategoryUseCase,
        deleteCategoryUseCase = deleteCategoryUseCase,
    )
    val categoryEditViewModel = CategoryEditViewModel(
        addCategoryUseCase = addCategoryUseCase,
        updateCategoryUseCase = updateCategoryUseCase,
    )
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

    val moviesListViewModel = MoviesListViewModel(
        getMoviesUseCase = getMoviesUseCase,
        addMovieUseCase = addMovieUseCase,
        updateMovieUseCase = updateMovieUseCase,
        deleteMovieUseCase = deleteMovieUseCase,
        searchMoviesUseCase = searchMoviesUseCase,
        getMoviesByCategoryUseCase = getMoviesByCategoryUseCase,
        getMoviesSortedUseCase = getMoviesSortedUseCase,
        getCategoriesUseCase = getCategoriesUseCase,
    )


    val movieDetailViewModel = MovieDetailViewModel(
        getMovieUseCase = getMovieUseCase,
        updateMovieWatchedStatusUseCase = updateMovieWatchedStatusUseCase,
        updateMovieUserRatingUseCase = updateMovieUserRatingUseCase,
        deleteMovieUseCase = deleteMovieUseCase,
        updateMovieUseCase = updateMovieUseCase,
        getCategoryUseCase = getCategoryUseCase,
    )

    val movieEditViewModel = MovieEditViewModel(
        addMovieUseCase = addMovieUseCase,
        updateMovieUseCase = updateMovieUseCase,
        getCategoriesUseCase = getCategoriesUseCase,
    )

    // Backup layer
    val exportBackupUseCase = ExportBackupUseCase(movieRepository)
    val importBackupUseCase = ImportBackupUseCase(movieRepository)

    val backupViewModel = BackupViewModel(
        exportBackupUseCase = exportBackupUseCase,
        importBackupUseCase = importBackupUseCase
    )


}
