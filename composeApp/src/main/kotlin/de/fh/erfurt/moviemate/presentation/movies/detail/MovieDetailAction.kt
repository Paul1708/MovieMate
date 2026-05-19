package de.fh.erfurt.moviemate.presentation.movies.detail

sealed interface MovieDetailAction {
    data object EditClicked : MovieDetailAction
    data object SaveEditClicked : MovieDetailAction
    data object CancelEditClicked : MovieDetailAction
    data object ImageClicked : MovieDetailAction

    data class WatchedChanged(val isWatched: Boolean) : MovieDetailAction
    data class TitleChanged(val value: String) : MovieDetailAction
    data class DescriptionChanged(val value: String) : MovieDetailAction
    data class ReleaseYearChanged(val value: String) : MovieDetailAction
    data class CategoryChanged(val value: String) : MovieDetailAction
    data class RuntimeChanged(val value: String) : MovieDetailAction
    data class UserRatingChanged(val value: String) : MovieDetailAction
    data class ImdbRatingChanged(val value: String) : MovieDetailAction
    data class CommentChanged(val value: String) : MovieDetailAction
    data class WatchProviderInputChanged(val value: String) : MovieDetailAction
    data object WatchProviderAdded : MovieDetailAction
    data class WatchProviderRemoved(val value: String) : MovieDetailAction
}
