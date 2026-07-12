package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.DeleteMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesUseCase
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
import de.fh.erfurt.moviemate.presentation.app.throwIfCancellation
import de.fh.erfurt.moviemate.presentation.movies.mapper.toHomeUiModel
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private var allMovies: List<Movie> = emptyList()
    private var categories: List<CategoryUiModel> = emptyList()
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.LoadRequested -> loadMovies(action.selectedCategoryId)
            is HomeUiAction.TabSelected -> updateState(selectedTab = action.tab)
            HomeUiAction.SearchClicked -> updateState(isSearchVisible = !_uiState.value.isSearchVisible)
            is HomeUiAction.SearchChanged -> updateState(searchQuery = action.query)
            HomeUiAction.FilterClicked -> updateState(isFilterDialogVisible = true)
            is HomeUiAction.CategorySelected -> updateState(selectedCategoryId = action.categoryId)
            is HomeUiAction.SortSelected -> updateState(selectedSortBy = action.sortCriterion)
            HomeUiAction.ClearCategoryFilterClicked -> updateState(
                selectedCategoryId = null,
                isFilterDialogVisible = false,
            )
            HomeUiAction.ClearSortClicked -> updateState(
                selectedSortBy = SortBy.UPDATED_AT,
                isFilterDialogVisible = false,
            )
            HomeUiAction.FilterDismissed -> updateState(isFilterDialogVisible = false)
            is HomeUiAction.MovieClicked -> emitEffect(HomeEffect.NavigateToDetail(action.movieId))
            is HomeUiAction.MovieLongPressed -> updateState(
                isDeleteMode = true,
                pendingDeleteMovieId = null,
            )
            is HomeUiAction.DeleteMovieClicked -> _uiState.update {
                it.copy(isDeleteMode = true, pendingDeleteMovieId = action.movieId)
            }
            HomeUiAction.ConfirmDeleteMovieClicked -> confirmDeleteMovie()
            HomeUiAction.DeleteMovieDismissed -> _uiState.update { it.copy(pendingDeleteMovieId = null) }
            HomeUiAction.ExitDeleteMode -> updateState(
                isDeleteMode = false,
                pendingDeleteMovieId = null,
            )
            HomeUiAction.AddClicked -> emitEffect(HomeEffect.NavigateToAdd)
            HomeUiAction.RetryClicked -> loadMovies()
        }
    }

    private fun loadMovies(selectedCategoryId: String? = _uiState.value.selectedCategoryId) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    selectedCategoryId = selectedCategoryId,
                )
            }

            runCatching {
                withContext(ioDispatcher) {
                    getCategoriesUseCase() to getMoviesUseCase()
                }
            }.onSuccess { (loadedCategories, loadedMovies) ->
                categories = loadedCategories.map(Category::toHomeCategoryUiModel)
                allMovies = loadedMovies
                updateState(
                    selectedCategoryId = selectedCategoryId,
                    isLoading = false,
                    errorMessage = null,
                )
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        movies = emptyList(),
                        availableCategories = categories,
                        isLoading = false,
                        errorMessage = throwable.message ?: "Movies could not be loaded.",
                        selectedCategoryId = selectedCategoryId,
                    )
                }
            }
        }
    }

    private fun confirmDeleteMovie() {
        val movieId = _uiState.value.pendingDeleteMovieId ?: return

        viewModelScope.launch {
            runCatching {
                withContext(ioDispatcher) {
                    deleteMovieUseCase(movieId)
                    getMoviesUseCase()
                }
            }.onSuccess { loadedMovies ->
                allMovies = loadedMovies
                val state = _uiState.value
                val hasVisibleMovies = visibleMovies(
                    selectedTab = state.selectedTab,
                    searchQuery = state.searchQuery,
                    selectedCategoryId = state.selectedCategoryId,
                    selectedSortBy = state.selectedSortBy,
                ).isNotEmpty()
                updateState(
                    isDeleteMode = hasVisibleMovies,
                    pendingDeleteMovieId = null,
                )
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        pendingDeleteMovieId = null,
                        errorMessage = throwable.message ?: "Movie could not be deleted.",
                    )
                }
            }
        }
    }

    private fun updateState(
        selectedTab: HomeTab = _uiState.value.selectedTab,
        searchQuery: String = _uiState.value.searchQuery,
        isSearchVisible: Boolean = _uiState.value.isSearchVisible,
        selectedCategoryId: String? = _uiState.value.selectedCategoryId,
        selectedSortBy: SortBy = _uiState.value.selectedSortBy,
        isFilterDialogVisible: Boolean = _uiState.value.isFilterDialogVisible,
        isDeleteMode: Boolean = _uiState.value.isDeleteMode,
        pendingDeleteMovieId: String? = _uiState.value.pendingDeleteMovieId,
        isLoading: Boolean = _uiState.value.isLoading,
        errorMessage: String? = _uiState.value.errorMessage,
    ) {
        _uiState.value = _uiState.value.copy(
            movies = visibleMovies(selectedTab, searchQuery, selectedCategoryId, selectedSortBy),
            availableCategories = categories,
            selectedTab = selectedTab,
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            selectedCategoryId = selectedCategoryId,
            selectedSortBy = selectedSortBy,
            isFilterDialogVisible = isFilterDialogVisible,
            isDeleteMode = isDeleteMode,
            pendingDeleteMovieId = pendingDeleteMovieId,
            isLoading = isLoading,
            errorMessage = errorMessage,
        )
    }

    private fun visibleMovies(
        selectedTab: HomeTab,
        searchQuery: String,
        selectedCategoryId: String?,
        selectedSortBy: SortBy,
    ): List<MovieUiModel> {
        return allMovies
            .filter { movie ->
                when (selectedTab) {
                    HomeTab.All -> true
                    HomeTab.Watched -> movie.isWatched
                    HomeTab.WatchLater -> !movie.isWatched
                }
            }
            .filter { movie -> searchQuery.isBlank() || movie.title.contains(searchQuery, ignoreCase = true) }
            .filter { movie -> selectedCategoryId == null || movie.categoryId == selectedCategoryId }
            .sortForHome(selectedSortBy)
            .map { movie -> movie.toHomeUiModel(categories) }
    }

    private fun emitEffect(effect: HomeEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}

sealed interface HomeEffect {
    data class NavigateToDetail(val movieId: String) : HomeEffect
    data object NavigateToAdd : HomeEffect
}

private fun Category.toHomeCategoryUiModel(): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        name = name,
        iconKey = iconKey ?: DefaultCategoryIconKey,
    )
}

private fun List<Movie>.sortForHome(sortBy: SortBy): List<Movie> {
    return when (sortBy) {
        SortBy.TITLE -> sortedBy { movie -> movie.title.lowercase() }
        SortBy.RELEASE_YEAR -> sortedByDescending { movie -> movie.releaseYear ?: 0 }
        SortBy.UPDATED_AT -> sortedByDescending { movie -> movie.updatedAtEpochMillis }
        SortBy.RATING -> sortedByDescending { movie -> movie.userRating ?: movie.imdbRating?.toInt() ?: 0 }
        SortBy.RUNTIME -> sortedByDescending { movie -> movie.runtimeMinutes ?: 0 }
    }
}
