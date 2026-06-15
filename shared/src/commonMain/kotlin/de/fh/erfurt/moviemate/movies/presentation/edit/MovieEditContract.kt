package de.fh.erfurt.moviemate.movies.presentation.edit

import de.fh.erfurt.moviemate.movies.presentation.list.CategoryListItemUiModel

data class MovieEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedMovieId: String? = null,
    val initialMovieId: String? = null, // Unterscheidung zwischen Neuanlage und Bearbeitung
    val showDiscardDialog: Boolean = false,
    val currentWatchProviders: List<String> = emptyList(),
    val availableCategories: List<CategoryListItemUiModel> = emptyList(),
)

sealed interface MovieEditEvent {
    data class SaveRequested(
        val title: String,
        val description: String,
        val releaseYear: Int? = null,
        val categoryName: String? = null,
        val posterUrl: String? = null,
        val runtimeMinutes: Int? = null,
        val userRating: Int? = null,
        val imdbRating: Double? = null,
        val isWatched: Boolean = false,
        val watchProviders: List<String> = emptyList(),
        val comment: String? = null,
        val updatedAtEpochMillis: Long,
    ) : MovieEditEvent

    // Steuerung des Discard-Dialogs
    data object BackClicked : MovieEditEvent
    data object KeepEditingRequested : MovieEditEvent
    data object DiscardChangesConfirmed : MovieEditEvent

    // Dynamische Listenverwaltung fuer Watch-Provider
    data class AddWatchProviderRequested(val provider: String) : MovieEditEvent
    data class RemoveWatchProviderRequested(val provider: String) : MovieEditEvent
}

sealed interface MovieEditEffect {
    data class ShowMessage(
        val message: String,
    ) : MovieEditEffect

    data object SavedSuccess : MovieEditEffect
    data object NavigateBack : MovieEditEffect // Ausloesung der Navigation nach DiscardConfirmed
}
