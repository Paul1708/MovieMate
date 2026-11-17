package de.fh.erfurt.moviemate.presentation.movies.detail

/**
 * User intents emitted by the detail UI and handled by MovieDetailScreenViewModel.
 */
sealed interface MovieDetailAction {
    data class LoadRequested(val movieId: String) : MovieDetailAction
    data object RetryClicked : MovieDetailAction
    data object EditClicked : MovieDetailAction
    data object SaveEditClicked : MovieDetailAction
    data object CancelEditClicked : MovieDetailAction
    data object ImageClicked : MovieDetailAction
    data class PosterSelected(val uri: String) : MovieDetailAction
    data object FetchMovieDataClicked : MovieDetailAction
    data class MovieMatchSelected(val imdbId: String) : MovieDetailAction
    data object MovieMatchesDismissed : MovieDetailAction

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
