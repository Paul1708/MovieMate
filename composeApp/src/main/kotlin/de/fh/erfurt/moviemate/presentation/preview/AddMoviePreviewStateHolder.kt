package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.presentation.movies.add.AddMovieAction
import de.fh.erfurt.moviemate.presentation.movies.add.AddMovieUiState

/**
 * Temporary in-memory state holder for the add movie mock UI.
 */
class AddMoviePreviewStateHolder {
    var state by mutableStateOf(AddMovieUiState(availableCategories = sampleCategories()))
        private set

    fun onAction(action: AddMovieAction) {
        when (action) {
            AddMovieAction.CancelClicked -> Unit
            AddMovieAction.SaveClicked -> Unit
            AddMovieAction.PosterClicked -> Unit
            AddMovieAction.FetchMovieDataClicked -> Unit
            is AddMovieAction.TitleChanged -> state = state.copy(title = action.value)
            is AddMovieAction.DescriptionChanged -> state = state.copy(description = action.value)
            is AddMovieAction.CategoryChanged -> state = state.copy(categoryId = action.value)
            is AddMovieAction.ReleaseYearChanged -> state = state.copy(releaseYear = action.value)
            is AddMovieAction.RuntimeChanged -> state = state.copy(runtimeMinutes = action.value)
            is AddMovieAction.UserRatingChanged -> state = state.copy(userRating = action.value)
            is AddMovieAction.ImdbRatingChanged -> state = state.copy(imdbRating = action.value)
            is AddMovieAction.WatchedChanged -> state = state.copy(isWatched = action.isWatched)
            is AddMovieAction.WatchProviderInputChanged -> {
                state = state.copy(watchProviderInput = action.value)
            }
            AddMovieAction.WatchProviderAdded -> addWatchProviders()
            is AddMovieAction.WatchProviderRemoved -> {
                state = state.copy(
                    watchProviders = state.watchProviders.filterNot { it == action.value }
                )
            }
            is AddMovieAction.CommentChanged -> state = state.copy(comment = action.value)
        }
    }

    private fun addWatchProviders() {
        val additions = state.watchProviderInput
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (additions.isEmpty()) return

        state = state.copy(
            watchProviders = (state.watchProviders + additions).distinctBy { it.lowercase() },
            watchProviderInput = ""
        )
    }
}
