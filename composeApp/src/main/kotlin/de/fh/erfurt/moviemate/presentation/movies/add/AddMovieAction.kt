package de.fh.erfurt.moviemate.presentation.movies.add

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
/**
 * User intents emitted by the add movie UI and handled by AddMovieViewModel.
 */
sealed interface AddMovieAction {
    data object LoadRequested : AddMovieAction
    data object CancelClicked : AddMovieAction
    data object SaveClicked : AddMovieAction
    data object PosterClicked : AddMovieAction
    data class PosterSelected(val uri: String) : AddMovieAction

    /** Fetches movie metadata for the current title from the configured provider. */
    data object FetchMovieDataClicked : AddMovieAction
    data class MovieMatchSelected(val imdbId: String) : AddMovieAction
    data object MovieMatchesDismissed : AddMovieAction
    data class AddCategoryNameChanged(val value: String) : AddMovieAction
    data class AddCategoryIconSelected(val iconKey: String) : AddMovieAction
    data object AddCategoryConfirmed : AddMovieAction
    data object AddCategoryDismissed : AddMovieAction

    data class TitleChanged(val value: String) : AddMovieAction
    data class DescriptionChanged(val value: String) : AddMovieAction
    data class CategoryChanged(val value: String) : AddMovieAction
    data class ReleaseYearChanged(val value: String) : AddMovieAction
    data class RuntimeChanged(val value: String) : AddMovieAction
    data class MediaTypeChanged(val value: MediaType) : AddMovieAction
    data class TotalSeasonsChanged(val value: String) : AddMovieAction
    data class UserRatingChanged(val value: String) : AddMovieAction
    data class ImdbRatingChanged(val value: String) : AddMovieAction
    data class WatchedChanged(val isWatched: Boolean) : AddMovieAction
    data class WatchProviderInputChanged(val value: String) : AddMovieAction
    data object WatchProviderAdded : AddMovieAction
    data class WatchProviderRemoved(val value: String) : AddMovieAction
    data class CommentChanged(val value: String) : AddMovieAction
}
