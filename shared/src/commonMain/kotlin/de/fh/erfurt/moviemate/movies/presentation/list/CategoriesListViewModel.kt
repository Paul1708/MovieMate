package de.fh.erfurt.moviemate.movies.presentation.list

import de.fh.erfurt.moviemate.movies.domain.usecase.category.DeleteCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.presentation.mapper.toListItemUiModel
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

class CategoriesListViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase

) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(CategoriesListUiState())
    val uiState: StateFlow<CategoriesListUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CategoriesListEffect>()
    val effect: SharedFlow<CategoriesListEffect> = _effect.asSharedFlow()


    fun onEvent(event: CategoriesListEvent) {
        when (event) {
            is CategoriesListEvent.LoadRequested -> loadCategories()
            is CategoriesListEvent.DeleteRequested -> deleteCategory(event.categoryId)
        }
    }

    private fun loadCategories() {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val categories = getCategoriesUseCase()
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        categories = categories.map { it.toListItemUiModel() }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                _effect.emit(CategoriesListEffect.ShowMessage(e.message ?: "An Error Occurred!"))
            }
        }
    }

    private fun deleteCategory(categoryId: String) {
        coroutineScope.launch {
            try {
                deleteCategoryUseCase(categoryId)
                loadCategories()
            } catch (e: Exception) {
                _effect.emit(CategoriesListEffect.ShowMessage(e.message ?: "failed to delete category."))
            }
        }
    }
}