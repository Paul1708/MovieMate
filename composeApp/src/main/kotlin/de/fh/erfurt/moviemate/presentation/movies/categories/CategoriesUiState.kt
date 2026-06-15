package de.fh.erfurt.moviemate.presentation.movies.categories

// Gesamter Anzeigezustand für den Kategorie-Screen.
data class CategoriesUiState(
    val categories: List<CategoryUiModel> = emptyList(),
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isDeleteMode: Boolean = false,
    val pendingDeleteCategoryId: String? = null,
    val isAddDialogVisible: Boolean = false,
    val addCategoryName: String = "",
    val selectedIconKey: String = DefaultCategoryIconKey,
    val addCategoryError: String? = null,
)
