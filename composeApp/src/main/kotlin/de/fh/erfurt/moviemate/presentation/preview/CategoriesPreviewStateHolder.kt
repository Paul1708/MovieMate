package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesUiAction
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesUiState
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel

/**
 * Temporary state holder for preview/demo category data.
 */
class CategoriesPreviewStateHolder(
    private val sourceMovies: List<Movie> = sampleMovies()
) {
    private val sourceCategories: List<CategoryUiModel>
        get() = previewCategories(sourceMovies)

    var state by mutableStateOf(CategoriesUiState(categories = sourceCategories))
        private set

    fun onAction(action: CategoriesUiAction) {
        when (action) {
            CategoriesUiAction.SearchClicked -> updateState(isSearchVisible = !state.isSearchVisible)
            is CategoriesUiAction.SearchChanged -> updateState(searchQuery = action.query)
            is CategoriesUiAction.CategoryClicked -> Unit
            CategoriesUiAction.RetryClicked -> updateState()
        }
    }

    private fun updateState(
        searchQuery: String = state.searchQuery,
        isSearchVisible: Boolean = state.isSearchVisible
    ) {
        state = state.copy(
            categories = filterCategories(searchQuery),
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            isLoading = false,
            errorMessage = null
        )
    }

    private fun filterCategories(searchQuery: String): List<CategoryUiModel> {
        if (searchQuery.isBlank()) return sourceCategories

        return sourceCategories.filter { category ->
            category.label.contains(searchQuery, ignoreCase = true)
        }
    }
}
