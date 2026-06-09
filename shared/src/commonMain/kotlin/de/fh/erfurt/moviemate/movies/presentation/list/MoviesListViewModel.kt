package de.fh.erfurt.moviemate.movies.presentation.list

import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.DeleteMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesByCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesSortedUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.SearchMoviesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieWatchedStatusUseCase

class MoviesListViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val addMovieUseCase: AddMovieUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase,
    private val getMoviesSortedUseCase: GetMoviesSortedUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,

    ) {
    val initialState = MoviesListUiState()

    fun onEvent(event: MoviesListEvent) {
        when (event) {
            is MoviesListEvent.AddRequested -> Unit
            is MoviesListEvent.DeleteRequested -> Unit
            is MoviesListEvent.FilterByCategoryRequested -> Unit
            is MoviesListEvent.LoadRequested -> Unit
            is MoviesListEvent.SearchRequested -> Unit
            is MoviesListEvent.UpdateRequested -> Unit
            is MoviesListEvent.SortRequested -> Unit
            is MoviesListEvent.FilterWatchedStatusRequested -> Unit
        }
    }
}