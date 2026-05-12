package de.fh.erfurt.moviemate.presentation.home

import de.fh.erfurt.moviemate.domain.model.CategoryType
import de.fh.erfurt.moviemate.domain.model.SortCriterion

// Aktionen, die der Homescreen an ein späteres ViewModel melden kann.
sealed interface HomeUiAction {
    data class TabSelected(val tab: HomeTab) : HomeUiAction
    data object SearchClicked : HomeUiAction
    data class SearchChanged(val query: String) : HomeUiAction
    data object FilterClicked : HomeUiAction
    data object SortClicked : HomeUiAction
    data class CategorySelected(val category: CategoryType?) : HomeUiAction
    data class SortSelected(val sortCriterion: SortCriterion) : HomeUiAction
    data object ClearCategoryFilterClicked : HomeUiAction
    data object ClearSortClicked : HomeUiAction
    data object FilterDismissed : HomeUiAction
    data object SortDismissed : HomeUiAction
    data class MovieClicked(val movieId: String) : HomeUiAction
    data object RetryClicked : HomeUiAction
}
