package de.fh.erfurt.moviemate.presentation.home

// Aktionen, die der Homescreen an ein späteres ViewModel melden kann.
sealed interface HomeUiAction {
    data class TabSelected(val tab: HomeTab) : HomeUiAction
    data object SearchClicked : HomeUiAction
    data class SearchChanged(val query: String) : HomeUiAction
    data object FilterClicked : HomeUiAction
    data class MovieClicked(val movieId: String) : HomeUiAction
    data object RetryClicked : HomeUiAction
}
