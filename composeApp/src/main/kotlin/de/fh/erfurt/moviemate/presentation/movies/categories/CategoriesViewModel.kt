package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.usecase.category.AddCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.DeleteCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMoviesUseCase
import de.fh.erfurt.moviemate.presentation.app.throwIfCancellation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private var allCategories: List<CategoryUiModel> = emptyList()
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(CategoriesUiState(isLoading = true))
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CategoriesEffect>()
    val effect: SharedFlow<CategoriesEffect> = _effect.asSharedFlow()

    fun onAction(action: CategoriesUiAction) {
        when (action) {
            CategoriesUiAction.LoadRequested -> loadCategories()
            CategoriesUiAction.SearchClicked -> updateState(isSearchVisible = !_uiState.value.isSearchVisible)
            is CategoriesUiAction.SearchChanged -> updateState(searchQuery = action.query)
            is CategoriesUiAction.CategoryClicked -> emitEffect(CategoriesEffect.NavigateToCategory(action.categoryId))
            is CategoriesUiAction.CategoryLongPressed -> updateState(
                isDeleteMode = true,
                pendingDeleteCategoryId = null,
            )
            is CategoriesUiAction.DeleteCategoryClicked -> _uiState.update {
                it.copy(isDeleteMode = true, pendingDeleteCategoryId = action.categoryId)
            }
            CategoriesUiAction.ConfirmDeleteCategoryClicked -> confirmDeleteCategory()
            CategoriesUiAction.DeleteCategoryDismissed -> _uiState.update {
                it.copy(pendingDeleteCategoryId = null)
            }
            CategoriesUiAction.ExitDeleteMode -> updateState(
                isDeleteMode = false,
                pendingDeleteCategoryId = null,
            )
            CategoriesUiAction.RetryClicked -> loadCategories()
            CategoriesUiAction.AddClicked -> _uiState.update {
                it.copy(
                    isDeleteMode = false,
                    pendingDeleteCategoryId = null,
                    isAddDialogVisible = true,
                    addCategoryName = "",
                    selectedIconKey = DefaultCategoryIconKey,
                    addCategoryError = null,
                )
            }
            is CategoriesUiAction.AddNameChanged -> _uiState.update {
                it.copy(addCategoryName = action.name, addCategoryError = null)
            }
            is CategoriesUiAction.AddIconSelected -> _uiState.update {
                it.copy(selectedIconKey = action.iconKey)
            }
            CategoriesUiAction.AddConfirmed -> addCategory()
            CategoriesUiAction.AddDismissed -> _uiState.update {
                it.copy(
                    isDeleteMode = false,
                    pendingDeleteCategoryId = null,
                    isAddDialogVisible = false,
                    addCategoryName = "",
                    selectedIconKey = DefaultCategoryIconKey,
                    addCategoryError = null,
                )
            }
        }
    }

    private fun loadCategories() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching { loadCategoryUiModels() }
                .onSuccess { categories ->
                    allCategories = categories
                    updateState(isLoading = false, errorMessage = null)
                }
                .onFailure { throwable ->
                    throwable.throwIfCancellation()
                    _uiState.update {
                        it.copy(
                            categories = emptyList(),
                            isLoading = false,
                            errorMessage = throwable.message ?: "Categories could not be loaded.",
                        )
                    }
                }
        }
    }

    private fun addCategory() {
        val state = _uiState.value
        val trimmedName = state.addCategoryName.trim()

        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(addCategoryError = "Name is required") }
            return
        }
        if (allCategories.any { category -> category.name.equals(trimmedName, ignoreCase = true) }) {
            _uiState.update { it.copy(addCategoryError = "Category already exists") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, addCategoryError = null) }
            runCatching {
                withContext(ioDispatcher) {
                    addCategoryUseCase(
                        CategoryDraft(
                            name = trimmedName,
                            iconKey = state.selectedIconKey,
                            updatedAtEpochMillis = System.currentTimeMillis(),
                        )
                    )
                }
                loadCategoryUiModels()
            }.onSuccess { categories ->
                allCategories = categories
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        isAddDialogVisible = false,
                        addCategoryName = "",
                        selectedIconKey = DefaultCategoryIconKey,
                        addCategoryError = null,
                    )
                }
                updateState(isLoading = false, errorMessage = null)
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        addCategoryError = throwable.message ?: "Category could not be saved.",
                    )
                }
            }
        }
    }

    private fun confirmDeleteCategory() {
        val categoryId = _uiState.value.pendingDeleteCategoryId ?: return

        viewModelScope.launch {
            runCatching {
                withContext(ioDispatcher) { deleteCategoryUseCase(categoryId) }
                loadCategoryUiModels()
            }.onSuccess { categories ->
                allCategories = categories
                val visibleCategories = filterCategories(_uiState.value.searchQuery)
                _uiState.update {
                    it.copy(
                        categories = visibleCategories,
                        isDeleteMode = visibleCategories.isNotEmpty(),
                        pendingDeleteCategoryId = null,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        pendingDeleteCategoryId = null,
                        errorMessage = throwable.message ?: "Category could not be deleted.",
                    )
                }
            }
        }
    }

    private suspend fun loadCategoryUiModels(): List<CategoryUiModel> {
        return withContext(ioDispatcher) {
            getCategoriesUseCase().toCategoryUiModels(getMoviesUseCase())
        }
    }

    private fun updateState(
        searchQuery: String = _uiState.value.searchQuery,
        isSearchVisible: Boolean = _uiState.value.isSearchVisible,
        isDeleteMode: Boolean = _uiState.value.isDeleteMode,
        pendingDeleteCategoryId: String? = _uiState.value.pendingDeleteCategoryId,
        isLoading: Boolean = _uiState.value.isLoading,
        errorMessage: String? = _uiState.value.errorMessage,
    ) {
        _uiState.value = _uiState.value.copy(
            categories = filterCategories(searchQuery),
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            isDeleteMode = isDeleteMode,
            pendingDeleteCategoryId = pendingDeleteCategoryId,
            isLoading = isLoading,
            errorMessage = errorMessage,
        )
    }

    private fun filterCategories(searchQuery: String): List<CategoryUiModel> {
        if (searchQuery.isBlank()) return allCategories
        return allCategories.filter { category -> category.name.contains(searchQuery, ignoreCase = true) }
    }

    private fun emitEffect(effect: CategoriesEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}

sealed interface CategoriesEffect {
    data class NavigateToCategory(val categoryId: String) : CategoriesEffect
}

private fun List<Category>.toCategoryUiModels(movies: List<Movie>): List<CategoryUiModel> {
    val movieCountByCategory = movies.groupingBy { movie -> movie.categoryId.orEmpty() }.eachCount()
    return map { category ->
        CategoryUiModel(
            id = category.id,
            name = category.name,
            iconKey = category.iconKey ?: DefaultCategoryIconKey,
            movieCountText = (movieCountByCategory[category.id] ?: 0).toMovieCountText(),
        )
    }
}

private fun Int.toMovieCountText(): String = if (this == 1) "1 Movie" else "$this Movies"
