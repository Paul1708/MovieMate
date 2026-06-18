package de.fh.erfurt.moviemate.movies.presentation.detail

import de.fh.erfurt.moviemate.movies.domain.usecase.category.DeleteCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoryUseCase
import de.fh.erfurt.moviemate.movies.presentation.mapper.toDetailUiModel
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

class CategoryDetailViewModel(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase

) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(CategoryDetailUiState())
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CategoryDetailEffect>()
    val effect: SharedFlow<CategoryDetailEffect> = _effect.asSharedFlow()

    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.LoadRequested -> loadCategory(event.categoryId)
            is CategoryDetailEvent.DeleteRequested -> deleteCategory(event.categoryId)        }
    }

    private fun loadCategory(categoryId: String) {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val category = getCategoryUseCase(categoryId)
                if (category != null) {
                    _uiState.update { it.copy(isLoading = false, category = category.toDetailUiModel()) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Category dose not exist.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun deleteCategory(categoryId: String) {
        coroutineScope.launch {
            try {
                deleteCategoryUseCase(categoryId)
                _effect.emit(CategoryDetailEffect.ShowMessage("Category deleted successfully."))
            } catch (e: Exception) {
                _effect.emit(CategoryDetailEffect.ShowMessage(e.message ?: "Failed to delete category."))
            }
        }
    }
}