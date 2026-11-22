package de.fh.erfurt.moviemate.presentation.movies.home

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

// Gesamter Anzeigezustand für den Homescreen.
data class HomeUiState(
    val movies: List<MovieUiModel> = emptyList(),
    val availableCategories: List<CategoryUiModel> = emptyList(),
    val selectedTab: HomeTab = HomeTab.All,
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val selectedCategoryId: String? = null,
    val selectedMediaType: MediaType? = null,
    val selectedSortBy: SortBy = SortBy.UPDATED_AT,
    val isFilterDialogVisible: Boolean = false,
    val isDeleteMode: Boolean = false,
    val pendingDeleteMovieId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
