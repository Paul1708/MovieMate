package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.domain.model.CategoryType
import de.fh.erfurt.moviemate.domain.model.SortCriterion

/**
 * Einfacher State-Holder für den Homescreen.
 *
 * Diese Klasse ist ein ViewModel-Skelett für die UI-Entwicklung. Sie arbeitet
 * noch mit Mockdaten, kapselt aber schon Suche, Tabs, Filter und Sortierung an
 * einer zentralen Stelle. Später kann hier `GetMoviesUseCase` angebunden werden.
 */
class HomeViewModel(
    private val sourceMovies: List<MovieUiModel> = previewHomeMovies()
) {
    var state by mutableStateOf(HomeUiState(movies = sourceMovies))
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
            movies = filterMovies(
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

    // Filtert lokal die Mockdaten. Später übernimmt das Repository diese Aufgabe.
    private fun filterMovies(
        selectedTab: HomeTab,
        searchQuery: String,
        selectedCategory: CategoryType?,
        selectedSortCriterion: SortCriterion
    ): List<MovieUiModel> {
        return sourceMovies
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
                selectedCategory == null || movie.categoryText == selectedCategory.displayName()
            }
            .sortForHome(selectedSortCriterion)
    }
}

// Sortiert die Mockdaten lokal, bis später Repository oder UseCase diese Aufgabe übernehmen.
private fun List<MovieUiModel>.sortForHome(sortCriterion: SortCriterion): List<MovieUiModel> {
    return when (sortCriterion) {
        SortCriterion.TITLE_ASC -> sortedBy { it.title.lowercase() }
        SortCriterion.TITLE_DESC -> sortedByDescending { it.title.lowercase() }
        SortCriterion.PUBLISHING_YEAR_DESC -> sortedByDescending { it.yearText.toIntOrNull() ?: 0 }
        SortCriterion.RATING_DESC -> sortedByDescending { it.ratingValue() }
        SortCriterion.RECENTLY_ADDED -> this
    }
}

// Macht aus dem angezeigten Bewertungstext einen Zahlenwert für die lokale Mock-Sortierung.
private fun MovieUiModel.ratingValue(): Double {
    return ratingText
        .substringBefore("/")
        .replace(',', '.')
        .toDoubleOrNull() ?: 0.0
}
