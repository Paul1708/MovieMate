package de.fh.erfurt.moviemate.presentation.categories

// Gesamter Anzeigezustand für den Kategorie-Screen.
// Später kann dieser State von einem echten ViewModel mit Backenddaten kommen.
data class CategoriesUiState(
    val categories: List<CategoryUiModel> = emptyList(),
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
