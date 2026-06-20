package de.fh.erfurt.moviemate.movies.presentation.list


data class CategoriesListUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryListItemUiModel> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface CategoriesListEvent {
    data object LoadRequested : CategoriesListEvent

    data class DeleteRequested(
        val categoryId: String,
    ) : CategoriesListEvent
}

sealed interface CategoriesListEffect {
    data class ShowMessage(
        val message: String,
    ) : CategoriesListEffect
}
