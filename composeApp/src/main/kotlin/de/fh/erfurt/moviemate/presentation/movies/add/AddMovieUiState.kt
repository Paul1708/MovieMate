package de.fh.erfurt.moviemate.presentation.movies.add

/**
 * Local form state for the add movie screen.
 *
 * Values stay as strings so the UI can keep incomplete user input. Real
 * validation and persistence can be added when the backend flow is connected.
 */
data class AddMovieUiState(
    val title: String = "",
    val description: String = "",
    val categoryId: String = "",
    val releaseYear: String = "",
    val runtimeMinutes: String = "",
    val userRating: String = "",
    val imdbRating: String = "",
    val isWatched: Boolean = false,
    val watchProviders: List<String> = emptyList(),
    val watchProviderInput: String = "",
    val comment: String = ""
) {
    val canSave: Boolean
        get() = title.isNotBlank()
}
