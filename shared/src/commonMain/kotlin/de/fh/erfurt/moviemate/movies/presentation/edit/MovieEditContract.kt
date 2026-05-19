package de.fh.erfurt.moviemate.movies.presentation.edit


data class MovieEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedMovieId: String? = null,
)

sealed interface MovieEditEvent {
    data class SaveRequested(
        val title: String,
        val description: String,
        val releaseYear: Int? = null,
        val categoryId: String? = null,
        val posterUrl: String? = null,
        val runtimeMinutes: Int? = null,
        val updatedAtEpochMillis: Long,
    ) : MovieEditEvent
}

sealed interface MovieEditEffect {
    data class ShowMessage(
        val message: String,
    ) : MovieEditEffect

    data object SavedSuccess : MovieEditEffect
}
