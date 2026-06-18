package de.fh.erfurt.moviemate.movies.presentation.detail

data class CategoryDetailUiState(
    val isLoading: Boolean = false,
    val category: CategoryDetailUiModel? = null,
    val errorMessage: String? = null,
)

data class CategoryDetailUiModel(
    val id: String,
    val name: String,
)

sealed interface CategoryDetailEvent {
    data class LoadRequested(
        val categoryId: String,
    ) : CategoryDetailEvent

    data class DeleteRequested(
        val categoryId: String,
    ) : CategoryDetailEvent
}

sealed interface CategoryDetailEffect {
    data class ShowMessage(
        val message: String,
    ) : CategoryDetailEffect
}
