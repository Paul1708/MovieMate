package de.fh.erfurt.moviemate.presentation.movies.categories

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

// Aktionen, die der Kategorie-Screen an seinen State-Holder meldet.
sealed interface CategoriesUiAction {
    data object SearchClicked : CategoriesUiAction
    data class SearchChanged(val query: String) : CategoriesUiAction
    data class CategoryClicked(val type: CategoryType) : CategoriesUiAction
    data object RetryClicked : CategoriesUiAction
}
