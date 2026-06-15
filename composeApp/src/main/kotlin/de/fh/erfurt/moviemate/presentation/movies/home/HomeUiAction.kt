package de.fh.erfurt.moviemate.presentation.movies.home

import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy

// Aktionen, die der Homescreen an ein späteres ViewModel melden kann.
sealed interface HomeUiAction {
    data class TabSelected(val tab: HomeTab) : HomeUiAction
    data object SearchClicked : HomeUiAction
    data class SearchChanged(val query: String) : HomeUiAction
    data object FilterClicked : HomeUiAction
    data class CategorySelected(val categoryId: String?) : HomeUiAction
    data class SortSelected(val sortCriterion: SortBy) : HomeUiAction
    data object ClearCategoryFilterClicked : HomeUiAction
    data object ClearSortClicked : HomeUiAction
    data object FilterDismissed : HomeUiAction
    data class MovieClicked(val movieId: String) : HomeUiAction
    data object AddClicked : HomeUiAction
    data object RetryClicked : HomeUiAction
}
