package de.fh.erfurt.moviemate.presentation.movies.categories

// Aktionen, die der Kategorie-Screen an seinen State-Holder meldet.
sealed interface CategoriesUiAction {
    data object SearchClicked : CategoriesUiAction
    data class SearchChanged(val query: String) : CategoriesUiAction
    data class CategoryClicked(val categoryId: String) : CategoriesUiAction
    data object RetryClicked : CategoriesUiAction
    data object AddClicked : CategoriesUiAction
    data class AddNameChanged(val name: String) : CategoriesUiAction
    data class AddIconSelected(val iconKey: String) : CategoriesUiAction
    data object AddConfirmed : CategoriesUiAction
    data object AddDismissed : CategoriesUiAction
}
