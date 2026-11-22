package de.fh.erfurt.moviemate.presentation.movies.home

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy

// Aktionen, die der Homescreen an HomeViewModel meldet.
sealed interface HomeUiAction {
    data class LoadRequested(val selectedCategoryId: String? = null) : HomeUiAction
    data class TabSelected(val tab: HomeTab) : HomeUiAction
    data object SearchClicked : HomeUiAction
    data class SearchChanged(val query: String) : HomeUiAction
    data object FilterClicked : HomeUiAction
    data class MediaTypeSelected(val mediaType: MediaType?) : HomeUiAction
    data class CategorySelected(val categoryId: String?) : HomeUiAction
    data class SortSelected(val sortCriterion: SortBy) : HomeUiAction
    data object ResetFiltersClicked : HomeUiAction
    data object FilterDismissed : HomeUiAction
    data class MovieClicked(val movieId: String) : HomeUiAction
    data class MovieLongPressed(val movieId: String) : HomeUiAction
    data class DeleteMovieClicked(val movieId: String) : HomeUiAction
    data object ConfirmDeleteMovieClicked : HomeUiAction
    data object DeleteMovieDismissed : HomeUiAction
    data object ExitDeleteMode : HomeUiAction
    data object AddClicked : HomeUiAction
    data object RetryClicked : HomeUiAction
}
