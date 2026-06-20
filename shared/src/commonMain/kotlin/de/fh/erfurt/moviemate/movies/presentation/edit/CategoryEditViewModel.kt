package de.fh.erfurt.moviemate.movies.presentation.edit

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.usecase.category.AddCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.UpdateCategoryUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryEditViewModel(
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getCategoryUseCase: GetCategoryUseCase

) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(CategoryEditUiState())
    val uiState: StateFlow<CategoryEditUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CategoryEditEffect>()
    val effect: SharedFlow<CategoryEditEffect> = _effect.asSharedFlow()

    fun onEvent(event: CategoryEditEvent) {
        when (event) {
            is CategoryEditEvent.LoadRequested -> loadCategory(event.categoryId)
            is CategoryEditEvent.SaveRequested -> saveCategory(event.name, event.updatedAtEpochMillis)
            is CategoryEditEvent.BackClicked -> _uiState.update { it.copy(showDiscardDialog = true)  }
            is CategoryEditEvent.KeepEditingRequested -> _uiState.update { it.copy(showDiscardDialog = false) }
            is CategoryEditEvent.DiscardChangesConfirmed -> {
                _uiState.update { it.copy(showDiscardDialog = false) }
                coroutineScope.launch { _effect.emit(CategoryEditEffect.NavigateBack) }
            }
        }
    }

    private fun loadCategory(categoryId: String?) {
        if (categoryId == null) return

        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, initialCategoryId = categoryId) }
            try {
                val category = getCategoryUseCase(categoryId)
                if (category != null) {
                    _uiState.update { it.copy(isLoading = false, name = category.name) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Category not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun saveCategory(name: String, updatedAtEpochMillis: Long) {
        coroutineScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val currentId = _uiState.value.initialCategoryId
                if (currentId != null) {
                    val existing = getCategoryUseCase(currentId) ?: throw IllegalStateException("This category no longer exists.")
                    updateCategoryUseCase(existing.copy(name = name, updatedAtEpochMillis = updatedAtEpochMillis))
                } else {
                    addCategoryUseCase(CategoryDraft(name = name, updatedAtEpochMillis = updatedAtEpochMillis))
                }

                _uiState.update { it.copy(isSaving = false) }
                _effect.emit(CategoryEditEffect.SavedSuccess)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
                _effect.emit(CategoryEditEffect.ShowMessage(e.message ?: "Save failed."))
            }
        }
    }
}