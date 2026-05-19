package de.fh.erfurt.moviemate.presentation.home

import androidx.lifecycle.ViewModel
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.mock.sampleMovies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel fuer den Homescreen.
 *
 * Aktuell arbeitet es noch mit Mockdaten, kapselt aber Screen-State und
 * Business-nahe UI-Logik zentral. Spaeter kann hier `GetMoviesUseCase`
 * angebunden werden.
 */
class HomeViewModel(
    private val sourceMovies: List<Movie> = sampleMovies(),
    initialCategory: CategoryType? = null
) : ViewModel() {
    private val _state = MutableStateFlow(
        HomeUiState(
            movies = sourceMovies.filterMovies(selectedCategory = initialCategory),
            selectedCategory = initialCategory
        )
    )
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.TabSelected -> updateState(selectedTab = action.tab)
            HomeUiAction.SearchClicked -> updateState(isSearchVisible = !state.value.isSearchVisible)
            is HomeUiAction.SearchChanged -> updateState(searchQuery = action.query)
            HomeUiAction.FilterClicked -> updateState(isFilterDialogVisible = true)
            HomeUiAction.SortClicked -> updateState(isSortDialogVisible = true)
            is HomeUiAction.CategorySelected -> updateState(selectedCategory = action.category)
            is HomeUiAction.SortSelected -> updateState(selectedSortBy = action.sortCriterion)
            HomeUiAction.ClearCategoryFilterClicked -> clearCategoryFilter()
            HomeUiAction.ClearSortClicked -> clearSort()
            HomeUiAction.FilterDismissed -> updateState(isFilterDialogVisible = false)
            HomeUiAction.SortDismissed -> updateState(isSortDialogVisible = false)
            is HomeUiAction.MovieClicked -> Unit
            HomeUiAction.RetryClicked -> refreshMovies()
        }
    }

    private fun updateState(
        selectedTab: HomeTab = state.value.selectedTab,
        searchQuery: String = state.value.searchQuery,
        isSearchVisible: Boolean = state.value.isSearchVisible,
        selectedCategory: CategoryType? = state.value.selectedCategory,
        selectedSortBy: SortBy = state.value.selectedSortBy,
        isFilterDialogVisible: Boolean = state.value.isFilterDialogVisible,
        isSortDialogVisible: Boolean = state.value.isSortDialogVisible
    ) {
        _state.value = state.value.copy(
            movies = sourceMovies.filterMovies(
                selectedTab = selectedTab,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                selectedSortBy = selectedSortBy
            ),
            selectedTab = selectedTab,
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            selectedCategory = selectedCategory,
            selectedSortBy = selectedSortBy,
            isFilterDialogVisible = isFilterDialogVisible,
            isSortDialogVisible = isSortDialogVisible,
            isLoading = false,
            errorMessage = null
        )
    }

    private fun clearCategoryFilter() {
        updateState(
            selectedCategory = null,
            isFilterDialogVisible = false
        )
    }

    private fun clearSort() {
        updateState(
            selectedSortBy = SortBy.UPDATED_AT,
            isSortDialogVisible = false
        )
    }

    private fun refreshMovies() {
        updateState()
    }

    // Filtert lokal die Mockdaten mit Domain-Werten. Später übernimmt das Repository diese Aufgabe.
    private fun List<Movie>.filterMovies(
        selectedTab: HomeTab = HomeTab.All,
        searchQuery: String = "",
        selectedCategory: CategoryType? = null,
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
                selectedCategory == null || movie.categoryId == selectedCategory.storageKey
            }
            .sortForHome(selectedSortBy)
            .map { movie -> movie.toUiModel() }
    }
}

// Sortiert die Mockdaten lokal, bis später Repository oder UseCase diese Aufgabe übernehmen.
private fun List<Movie>.sortForHome(sortCriterion: SortBy): List<Movie> {
    return when (sortCriterion) {
        SortBy.TITLE -> sortedBy { it.title.lowercase() }
        SortBy.RELEASE_YEAR -> sortedByDescending { it.releaseYear ?: 0 }
        SortBy.UPDATED_AT -> sortedByDescending { it.updatedAtEpochMillis }
        SortBy.RATING -> sortedByDescending { it.userRating ?: it.imdbRating?.toInt() ?: 0 }
        SortBy.RUNTIME -> sortedByDescending { it.runtimeMinutes ?: 0 }
    }
}
