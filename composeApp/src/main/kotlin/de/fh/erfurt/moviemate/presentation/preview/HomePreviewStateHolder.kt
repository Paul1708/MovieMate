package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.home.HomeTab
import de.fh.erfurt.moviemate.presentation.movies.home.HomeUiAction
import de.fh.erfurt.moviemate.presentation.movies.home.HomeUiState
import de.fh.erfurt.moviemate.presentation.movies.mapper.toHomeUiModel
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

/**
 * Temporary state holder for the Android UI demo.
 */
class HomePreviewStateHolder(
    sourceMovies: List<Movie> = sampleMovies(),
    private val categories: List<CategoryUiModel> = sampleCategories(),
    initialCategoryId: String? = null
) {
    private var allMovies: List<Movie> = sourceMovies

    var state by mutableStateOf(
        HomeUiState(
            movies = allMovies.filterMovies(selectedCategoryId = initialCategoryId),
            availableCategories = categories,
            selectedCategoryId = initialCategoryId
        )
    )
        private set

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
                isFilterDialogVisible = false
            )
            HomeUiAction.ClearSortClicked -> updateState(
                selectedSortBy = SortBy.UPDATED_AT,
                isFilterDialogVisible = false
            )
            HomeUiAction.FilterDismissed -> updateState(isFilterDialogVisible = false)
            is HomeUiAction.MovieClicked -> Unit
            is HomeUiAction.MovieLongPressed -> updateState(
                isDeleteMode = true,
                pendingDeleteMovieId = null
            )
            is HomeUiAction.DeleteMovieClicked -> state = state.copy(
                isDeleteMode = true,
                pendingDeleteMovieId = action.movieId
            )
            HomeUiAction.ConfirmDeleteMovieClicked -> confirmDeleteMovie()
            HomeUiAction.DeleteMovieDismissed -> state = state.copy(pendingDeleteMovieId = null)
            HomeUiAction.ExitDeleteMode -> updateState(
                isDeleteMode = false,
                pendingDeleteMovieId = null
            )
            HomeUiAction.AddClicked -> Unit
            HomeUiAction.RetryClicked -> updateState()
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
    ) {
        state = state.copy(
            movies = allMovies.filterMovies(
                selectedTab = selectedTab,
                searchQuery = searchQuery,
                selectedCategoryId = selectedCategoryId,
                selectedSortBy = selectedSortBy
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
            isLoading = false,
            errorMessage = null
        )
    }

    private fun confirmDeleteMovie() {
        val movieId = state.pendingDeleteMovieId ?: return
        deleteMovie(movieId)
    }

    private fun deleteMovie(movieId: String) {
        allMovies = allMovies.filterNot { movie -> movie.id == movieId }
        val hasVisibleMovies = allMovies.filterMovies(
            selectedTab = state.selectedTab,
            searchQuery = state.searchQuery,
            selectedCategoryId = state.selectedCategoryId,
            selectedSortBy = state.selectedSortBy
        ).isNotEmpty()
        updateState(
            isDeleteMode = hasVisibleMovies,
            pendingDeleteMovieId = null
        )
    }

    private fun List<Movie>.filterMovies(
        selectedTab: HomeTab = HomeTab.All,
        searchQuery: String = "",
        selectedCategoryId: String? = null,
        selectedSortBy: SortBy = SortBy.UPDATED_AT
    ): List<MovieUiModel> {
        return this
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

private fun List<Movie>.sortForHome(sortBy: SortBy): List<Movie> {
    return when (sortBy) {
        SortBy.TITLE -> sortedBy { it.title.lowercase() }
        SortBy.RELEASE_YEAR -> sortedByDescending { it.releaseYear ?: 0 }
        SortBy.UPDATED_AT -> sortedByDescending { it.updatedAtEpochMillis }
        SortBy.RATING -> sortedByDescending { it.userRating ?: it.imdbRating?.toInt() ?: 0 }
        SortBy.RUNTIME -> sortedByDescending { it.runtimeMinutes ?: 0 }
    }
}
