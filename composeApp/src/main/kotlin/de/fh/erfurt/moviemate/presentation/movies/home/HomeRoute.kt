package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
import de.fh.erfurt.moviemate.presentation.movies.mapper.toHomeUiModel
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    initialCategoryId: String? = null,
    modifier: Modifier = Modifier,
    onMovieSelected: (String) -> Unit = {},
    onAddClicked: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val stateHolder = remember(appContainer, ioDispatcher, scope) {
        HomeRouteStateHolder(
            appContainer = appContainer,
            ioDispatcher = ioDispatcher,
            scope = scope,
            initialCategoryId = initialCategoryId,
        )
    }

    LaunchedEffect(stateHolder, initialCategoryId) {
        stateHolder.loadMovies(selectedCategoryId = initialCategoryId)
    }

    HomeScreen(
        state = stateHolder.state,
        onAction = { action ->
            when (action) {
                is HomeUiAction.MovieClicked -> onMovieSelected(action.movieId)
                HomeUiAction.AddClicked -> onAddClicked()
                else -> stateHolder.onAction(action)
            }
        },
        modifier = modifier,
    )
}

private class HomeRouteStateHolder(
    private val appContainer: AppContainer?,
    private val ioDispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
    initialCategoryId: String?,
) {
    private var allMovies: List<Movie> = emptyList()
    private var categories: List<CategoryUiModel> = emptyList()

    var state by mutableStateOf(
        HomeUiState(
            selectedCategoryId = initialCategoryId,
            isLoading = true,
        )
    )
        private set

    fun loadMovies(selectedCategoryId: String? = state.selectedCategoryId) {
        val container = appContainer
        if (container == null) {
            state = state.copy(
                isLoading = false,
                errorMessage = "Movies are only available in the running app.",
                selectedCategoryId = selectedCategoryId,
            )
            return
        }

        scope.launch {
            state = state.copy(
                isLoading = true,
                errorMessage = null,
                selectedCategoryId = selectedCategoryId,
            )

            runCatching {
                withContext(ioDispatcher) {
                    val loadedCategories = container.getCategoriesUseCase()
                    val loadedMovies = container.getMoviesUseCase()
                    loadedCategories to loadedMovies
                }
            }.onSuccess { (loadedCategories, loadedMovies) ->
                categories = loadedCategories.map { category -> category.toUiModel() }
                allMovies = loadedMovies
                updateState(
                    selectedCategoryId = selectedCategoryId,
                    isLoading = false,
                    errorMessage = null,
                )
            }.onFailure { throwable ->
                state = state.copy(
                    movies = emptyList(),
                    availableCategories = categories,
                    isLoading = false,
                    errorMessage = throwable.message ?: "Movies could not be loaded.",
                    selectedCategoryId = selectedCategoryId,
                )
            }
        }
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.TabSelected -> updateState(selectedTab = action.tab)
            HomeUiAction.SearchClicked -> updateState(isSearchVisible = !state.isSearchVisible)
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
            is HomeUiAction.MovieClicked -> Unit
            is HomeUiAction.MovieLongPressed -> updateState(
                isDeleteMode = true,
                pendingDeleteMovieId = null,
            )
            is HomeUiAction.DeleteMovieClicked -> state = state.copy(
                isDeleteMode = true,
                pendingDeleteMovieId = action.movieId,
            )
            HomeUiAction.ConfirmDeleteMovieClicked -> confirmDeleteMovie()
            HomeUiAction.DeleteMovieDismissed -> state = state.copy(pendingDeleteMovieId = null)
            HomeUiAction.ExitDeleteMode -> updateState(
                isDeleteMode = false,
                pendingDeleteMovieId = null,
            )
            HomeUiAction.AddClicked -> Unit
            HomeUiAction.RetryClicked -> loadMovies()
        }
    }

    private fun confirmDeleteMovie() {
        val movieId = state.pendingDeleteMovieId ?: return
        val container = appContainer ?: return

        scope.launch {
            runCatching {
                withContext(ioDispatcher) {
                    container.deleteMovieUseCase(movieId)
                    container.getMoviesUseCase()
                }
            }.onSuccess { loadedMovies ->
                allMovies = loadedMovies
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
                state = state.copy(
                    pendingDeleteMovieId = null,
                    errorMessage = throwable.message ?: "Movie could not be deleted.",
                )
            }
        }
    }

    private fun updateState(
        selectedTab: HomeTab = state.selectedTab,
        searchQuery: String = state.searchQuery,
        isSearchVisible: Boolean = state.isSearchVisible,
        selectedCategoryId: String? = state.selectedCategoryId,
        selectedSortBy: SortBy = state.selectedSortBy,
        isFilterDialogVisible: Boolean = state.isFilterDialogVisible,
        isDeleteMode: Boolean = state.isDeleteMode,
        pendingDeleteMovieId: String? = state.pendingDeleteMovieId,
        isLoading: Boolean = state.isLoading,
        errorMessage: String? = state.errorMessage,
    ) {
        state = state.copy(
            movies = visibleMovies(
                selectedTab = selectedTab,
                searchQuery = searchQuery,
                selectedCategoryId = selectedCategoryId,
                selectedSortBy = selectedSortBy,
            ),
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
            .filter { movie ->
                searchQuery.isBlank() || movie.title.contains(searchQuery, ignoreCase = true)
            }
            .filter { movie ->
                selectedCategoryId == null || movie.categoryId == selectedCategoryId
            }
            .sortForHome(selectedSortBy)
            .map { movie -> movie.toHomeUiModel(categories) }
    }
}

private fun Category.toUiModel(): CategoryUiModel {
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
