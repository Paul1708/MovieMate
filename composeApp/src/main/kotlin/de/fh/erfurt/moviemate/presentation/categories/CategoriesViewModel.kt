package de.fh.erfurt.moviemate.presentation.categories

import androidx.lifecycle.ViewModel
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.presentation.mock.sampleMovies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel fuer den Kategorie-Screen.
 *
 * Aktuell arbeitet es nur mit Mockdaten. Die Struktur bleibt aber so, dass
 * Kategorien und Filmzahlen spaeter aus UseCases oder Repository-Daten
 * berechnet werden koennen.
 */
class CategoriesViewModel(
    private val sourceMovies: List<Movie> = sampleMovies()
) : ViewModel() {
    private val sourceCategories: List<CategoryUiModel>
        get() = previewCategories(sourceMovies)

    private val _state = MutableStateFlow(CategoriesUiState(categories = sourceCategories))
    val state: StateFlow<CategoriesUiState> = _state.asStateFlow()

    fun onAction(action: CategoriesUiAction) {
        when (action) {
            CategoriesUiAction.SearchClicked -> updateState(isSearchVisible = !state.value.isSearchVisible)
            is CategoriesUiAction.SearchChanged -> updateState(searchQuery = action.query)
            is CategoriesUiAction.CategoryClicked -> Unit
            CategoriesUiAction.RetryClicked -> updateState()
        }
    }

    private fun updateState(
        searchQuery: String = state.value.searchQuery,
        isSearchVisible: Boolean = state.value.isSearchVisible
    ) {
        _state.value = state.value.copy(
            categories = filterCategories(searchQuery),
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            isLoading = false,
            errorMessage = null
        )
    }

    // Filtert die Mock-Kategorien lokal. Später kann diese Liste aus echten Filmen entstehen.
    private fun filterCategories(searchQuery: String): List<CategoryUiModel> {
        if (searchQuery.isBlank()) return sourceCategories

        return sourceCategories.filter { category ->
            category.label.contains(searchQuery, ignoreCase = true)
        }
    }
}
