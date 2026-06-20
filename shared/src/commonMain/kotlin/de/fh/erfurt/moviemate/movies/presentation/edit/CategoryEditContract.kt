package de.fh.erfurt.moviemate.movies.presentation.edit

data class CategoryEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedCategoryId: String? = null,
    val initialCategoryId: String? = null,
    val name: String = "",
    val selectedIconKey: String? = null,
    val showDiscardDialog: Boolean = false,
)

sealed interface CategoryEditEvent {

    data class LoadRequested(val categoryId: String?) : CategoryEditEvent

    data class SaveRequested(
        val name: String,
        val updatedAtEpochMillis: Long
        val iconKey: String? = null,
    ) : CategoryEditEvent

    data object BackClicked : CategoryEditEvent
    data object KeepEditingRequested : CategoryEditEvent
    data object DiscardChangesConfirmed : CategoryEditEvent
}

sealed interface CategoryEditEffect {
    data class ShowMessage(
        val message: String,
    ) : CategoryEditEffect

    data object SavedSuccess : CategoryEditEffect
    data object NavigateBack : CategoryEditEffect
}
