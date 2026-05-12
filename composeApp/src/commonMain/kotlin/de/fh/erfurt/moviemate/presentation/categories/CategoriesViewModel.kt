package de.fh.erfurt.moviemate.presentation.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Einfacher State-Holder für den Kategorie-Screen.
 *
 * Diese Klasse arbeitet aktuell nur mit Mockdaten. Sie hält aber schon die
 * Struktur bereit, damit später Kategorien und Filmzahlen aus UseCases oder
 * Repository-Daten berechnet werden können.
 */
class CategoriesViewModel(
    private val sourceCategories: List<CategoryUiModel> = previewCategories()
) {
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

    // Filtert die Mock-Kategorien lokal. Später kann diese Liste aus echten Filmen entstehen.
    private fun filterCategories(searchQuery: String): List<CategoryUiModel> {
        if (searchQuery.isBlank()) return sourceCategories

        return sourceCategories.filter { category ->
            category.label.contains(searchQuery, ignoreCase = true)
        }
    }
}
