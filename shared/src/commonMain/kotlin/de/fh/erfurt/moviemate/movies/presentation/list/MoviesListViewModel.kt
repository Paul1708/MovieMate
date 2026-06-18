package de.fh.erfurt.moviemate.movies.presentation.list

import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.DeleteMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesByCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesSortedUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.SearchMoviesUseCase
import de.fh.erfurt.moviemate.movies.presentation.mapper.toListItemUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesListViewModel(
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase,
    private val getMoviesSortedUseCase: GetMoviesSortedUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,

    ) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(MoviesListUiState())
    val uiState: StateFlow<MoviesListUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<MoviesListEffect>()
    val effect: SharedFlow<MoviesListEffect> = _effect.asSharedFlow()

    // Status für Filter und Sortierung
    private var currentCategoryId: String? = null
    private var currentQuery: String? = null
    private var currentSortBy: SortBy = SortBy.TITLE
    private var currentAscending: Boolean = true
    private var currentWatchedFilter: Boolean? = null

    fun onEvent(event: MoviesListEvent) {
        when (event) {
            is MoviesListEvent.LoadRequested -> loadMovies()
            is MoviesListEvent.FilterByCategoryRequested -> {
                currentCategoryId = event.categoryId
                currentQuery = null
                loadMovies()
            }
            is MoviesListEvent.SearchRequested -> {
                currentQuery = event.query
                currentCategoryId = null
                loadMovies()
            }
            is MoviesListEvent.SortRequested -> {
                currentSortBy = runCatching { SortBy.valueOf(event.sortBy.uppercase()) }.getOrDefault(SortBy.TITLE)
                currentAscending = event.ascending
                loadMovies()
            }
            is MoviesListEvent.FilterWatchedStatusRequested -> {
                currentWatchedFilter = event.isWatched
                loadMovies()
            }
            is MoviesListEvent.DeleteRequested -> deleteMovie(event.movieId)
        }
    }

    private fun loadMovies() {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val categories = getCategoriesUseCase()
                val categoryMap = categories.associate { it.id to it.name }

                val movies = if (!currentQuery.isNullOrBlank()) {
                    searchMoviesUseCase(currentQuery!!)
                } else if (currentCategoryId != null) {
                    getMoviesByCategoryUseCase(currentCategoryId!!)
                } else {
                    getMoviesSortedUseCase(currentSortBy, currentAscending)
                }

                val filteredMovies = if (currentWatchedFilter != null) {
                    movies.filter { it.isWatched == currentWatchedFilter }
                } else {
                    movies
                }

                val uiModels = filteredMovies.map { movie ->
                    movie.toListItemUiModel(categoryMap[movie.categoryId])
                }

                _uiState.update { it.copy(isLoading = false, movies = uiModels) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                _effect.emit(MoviesListEffect.ShowMessage(e.message ?: "An Error Occurred!."))
            }
        }
    }

    private fun deleteMovie(movieId: String) {
        coroutineScope.launch {
            try {
                deleteMovieUseCase(movieId)
                loadMovies()
            } catch (e: Exception) {
                _effect.emit(MoviesListEffect.ShowMessage(e.message ?: "Field to delete the film."))
            }
        }
    }

}