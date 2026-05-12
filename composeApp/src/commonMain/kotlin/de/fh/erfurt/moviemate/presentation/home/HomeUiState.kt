package de.fh.erfurt.moviemate.presentation.home

import de.fh.erfurt.moviemate.domain.model.CategoryType
import de.fh.erfurt.moviemate.domain.model.SortCriterion

// Gesamter Anzeigezustand für den Homescreen.
// Später kann dieser State direkt von einem ViewModel geliefert werden.
data class HomeUiState(
    val movies: List<MovieUiModel> = emptyList(),
    val selectedTab: HomeTab = HomeTab.All,
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val selectedCategory: CategoryType? = null,
    val selectedSortCriterion: SortCriterion = SortCriterion.RECENTLY_ADDED,
    val isFilterDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
