package de.fh.erfurt.moviemate.presentation.movies.add

import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
import de.fh.erfurt.moviemate.presentation.movies.components.MovieLookupState

/**
 * Draft form state owned by AddMovieViewModel.
 * Values stay as strings so the UI can keep incomplete user input.
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
    val lookup: MovieLookupState = MovieLookupState(),
    val isAddingCategory: Boolean = false,
    val isSaving: Boolean = false,
    val isAddCategoryDialogVisible: Boolean = false,
    val addCategoryName: String = "",
    val addCategoryIconKey: String = DefaultCategoryIconKey,
    val addCategoryError: String? = null,
    val errorMessage: String? = null,
) {
    val isFetching: Boolean get() = lookup.isLoading

    val canSave: Boolean
        get() = title.isNotBlank() &&
            description.isNotBlank() &&
            !isLoading &&
            !isFetching &&
            lookup.results == null &&
            !isAddingCategory &&
            !isAddCategoryDialogVisible &&
            !isSaving
}
