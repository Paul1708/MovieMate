package de.fh.erfurt.moviemate.movies.presentation.edit

import de.fh.erfurt.moviemate.movies.domain.usecase.category.AddCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.UpdateCategoryUseCase

class CategoryEditViewModel(
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
) {
    val initialState = CategoryEditUiState()

    fun onEvent(event: CategoryEditEvent) {
        when (event) {
            is CategoryEditEvent.SaveRequested -> Unit
            is CategoryEditEvent.BackClicked -> Unit
            is CategoryEditEvent.KeepEditingRequested -> Unit
            is CategoryEditEvent.DiscardChangesConfirmed -> Unit
        }
    }
}