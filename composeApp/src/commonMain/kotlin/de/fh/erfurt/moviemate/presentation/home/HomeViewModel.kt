package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.domain.model.CategoryType
import de.fh.erfurt.moviemate.domain.model.Movie
import de.fh.erfurt.moviemate.domain.model.SortCriterion
import de.fh.erfurt.moviemate.presentation.mock.sampleMovies

/**
 * Einfaches ViewModel-Skelett für den Homescreen.
 *
 * Diese Klasse arbeitet noch mit Mockdaten, kapselt aber schon Suche, Tabs,
 * Filter und Sortierung an einer zentralen Stelle. Später kann hier
 * `GetMoviesUseCase` angebunden werden.
 */
class HomeViewModel(
    private val sourceMovies: List<Movie> = sampleMovies(),
    initialCategory: CategoryType? = null
) {
    var state by mutableStateOf(
        HomeUiState(
            movies = sourceMovies.filterMovies(selectedCategory = initialCategory),
            selectedCategory = initialCategory
        )
    )
        private set

    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.TabSelected -> updateState(selectedTab = action.tab)
            HomeUiAction.SearchClicked -> updateState(isSearchVisible = !state.isSearchVisible)
            is HomeUiAction.SearchChanged -> updateState(searchQuery = action.query)
            HomeUiAction.FilterClicked -> updateState(isFilterDialogVisible = true)
            HomeUiAction.SortClicked -> updateState(isSortDialogVisible = true)
            is HomeUiAction.CategorySelected -> updateState(selectedCategory = action.category)
            is HomeUiAction.SortSelected -> updateState(selectedSortCriterion = action.sortCriterion)
            HomeUiAction.ClearCategoryFilterClicked -> clearCategoryFilter()
            HomeUiAction.ClearSortClicked -> clearSort()
            HomeUiAction.FilterDismissed -> updateState(isFilterDialogVisible = false)
            HomeUiAction.SortDismissed -> updateState(isSortDialogVisible = false)
            is HomeUiAction.MovieClicked -> Unit
            HomeUiAction.RetryClicked -> refreshMovies()
        }
    }

    private fun updateState(
        selectedTab: HomeTab = state.selectedTab,
        searchQuery: String = state.searchQuery,
        isSearchVisible: Boolean = state.isSearchVisible,
        selectedCategory: CategoryType? = state.selectedCategory,
        selectedSortCriterion: SortCriterion = state.selectedSortCriterion,
        isFilterDialogVisible: Boolean = state.isFilterDialogVisible,
        isSortDialogVisible: Boolean = state.isSortDialogVisible
    ) {
        state = state.copy(
            movies = sourceMovies.filterMovies(
                selectedTab = selectedTab,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                selectedSortCriterion = selectedSortCriterion
            ),
            selectedTab = selectedTab,
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            selectedCategory = selectedCategory,
            selectedSortCriterion = selectedSortCriterion,
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
            selectedSortCriterion = SortCriterion.RECENTLY_ADDED,
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
        selectedSortCriterion: SortCriterion = SortCriterion.RECENTLY_ADDED
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
                selectedCategory == null || movie.category.type == selectedCategory
            }
            .sortForHome(selectedSortCriterion)
            .map { movie -> movie.toUiModel() }
    }
}

// Sortiert die Mockdaten lokal, bis später Repository oder UseCase diese Aufgabe übernehmen.
private fun List<Movie>.sortForHome(sortCriterion: SortCriterion): List<Movie> {
    return when (sortCriterion) {
        SortCriterion.TITLE_ASC -> sortedBy { it.title.lowercase() }
        SortCriterion.TITLE_DESC -> sortedByDescending { it.title.lowercase() }
        SortCriterion.PUBLISHING_YEAR_DESC -> sortedByDescending { it.publishingYear }
        SortCriterion.RATING_DESC -> sortedByDescending { it.rating }
        SortCriterion.RECENTLY_ADDED -> this
    }
}
