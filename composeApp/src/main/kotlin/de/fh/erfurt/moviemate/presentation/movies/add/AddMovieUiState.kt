package de.fh.erfurt.moviemate.presentation.movies.add

import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel

/**
 * Local form state for the add movie screen.
 *
 * Values stay as strings so the UI can keep incomplete user input. Real
 * validation and persistence can be added when the backend flow is connected.
 */
data class AddMovieUiState(
    val title: String = "",
    val description: String = "",
    val posterUrl: String? = null,
    val categoryId: String = "",
    val releaseYear: String = "",
    val runtimeMinutes: String = "",
    val userRating: String = "",
    val imdbRating: String = "",
    val isWatched: Boolean = false,
    val watchProviders: List<String> = emptyList(),
    val watchProviderInput: String = "",
    val comment: String = "",
    val availableCategories: List<CategoryUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && !isLoading && !isSaving
}
