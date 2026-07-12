package de.fh.erfurt.moviemate.presentation.movies.add

/**
 * User intents emitted by the add movie UI and handled by AddMovieViewModel.
 */
sealed interface AddMovieAction {
    data object LoadRequested : AddMovieAction
    data object CancelClicked : AddMovieAction
    data object SaveClicked : AddMovieAction
    data object PosterClicked : AddMovieAction
    data class PosterSelected(val uri: String) : AddMovieAction

    /** Reserved for the planned external movie-metadata lookup. */
    data object FetchMovieDataClicked : AddMovieAction

    data class TitleChanged(val value: String) : AddMovieAction
    data class DescriptionChanged(val value: String) : AddMovieAction
    data class CategoryChanged(val value: String) : AddMovieAction
    data class ReleaseYearChanged(val value: String) : AddMovieAction
    data class RuntimeChanged(val value: String) : AddMovieAction
    data class UserRatingChanged(val value: String) : AddMovieAction
    data class ImdbRatingChanged(val value: String) : AddMovieAction
    data class WatchedChanged(val isWatched: Boolean) : AddMovieAction
    data class WatchProviderInputChanged(val value: String) : AddMovieAction
    data object WatchProviderAdded : AddMovieAction
    data class WatchProviderRemoved(val value: String) : AddMovieAction
    data class CommentChanged(val value: String) : AddMovieAction
}
