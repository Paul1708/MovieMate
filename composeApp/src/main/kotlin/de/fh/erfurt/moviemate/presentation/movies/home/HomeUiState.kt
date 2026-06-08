package de.fh.erfurt.moviemate.presentation.movies.home

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

// Gesamter Anzeigezustand für den Homescreen.
// Später kann dieser State direkt von einem ViewModel geliefert werden.
data class HomeUiState(
    val movies: List<MovieUiModel> = emptyList(),
    val selectedTab: HomeTab = HomeTab.All,
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val selectedCategory: CategoryType? = null,
    val selectedSortBy: SortBy = SortBy.UPDATED_AT,
    val isFilterDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
