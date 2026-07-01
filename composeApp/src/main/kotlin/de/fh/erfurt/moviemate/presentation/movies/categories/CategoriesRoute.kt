package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CategoriesRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    modifier: Modifier = Modifier,
    onCategorySelected: (String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val stateHolder = remember(appContainer, ioDispatcher, scope) {
        CategoriesRouteStateHolder(
            appContainer = appContainer,
            ioDispatcher = ioDispatcher,
            scope = scope,
        )
    }

    LaunchedEffect(stateHolder) {
        stateHolder.loadCategories()
    }

    CategoriesScreen(
        state = stateHolder.state,
        onAction = { action ->
            when (action) {
                is CategoriesUiAction.CategoryClicked -> onCategorySelected(action.categoryId)
                else -> stateHolder.onAction(action)
            }
        },
        modifier = modifier,
    )
}

private class CategoriesRouteStateHolder(
    private val appContainer: AppContainer?,
    private val ioDispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
) {
    private var allCategories: List<CategoryUiModel> = emptyList()

    var state by mutableStateOf(CategoriesUiState(isLoading = true))
        private set

    fun loadCategories() {
        val container = appContainer
        if (container == null) {
            state = state.copy(
                isLoading = false,
                errorMessage = "Categories are only available in the running app.",
            )
            return
        }

        scope.launch {
            state = state.copy(isLoading = true, errorMessage = null)

            runCatching {
                withContext(ioDispatcher) {
                    val categories = container.getCategoriesUseCase()
                    val movies = container.getMoviesUseCase()
                    categories.toCategoryUiModels(movies)
                }
            }.onSuccess { categories ->
                allCategories = categories
                updateState(
                    isLoading = false,
                    errorMessage = null,
                )
            }.onFailure { throwable ->
                state = state.copy(
                    categories = emptyList(),
                    isLoading = false,
                    errorMessage = throwable.message ?: "Categories could not be loaded.",
                )
            }
        }
    }

    fun onAction(action: CategoriesUiAction) {
        when (action) {
            CategoriesUiAction.SearchClicked -> updateState(isSearchVisible = !state.isSearchVisible)
            is CategoriesUiAction.SearchChanged -> updateState(searchQuery = action.query)
            is CategoriesUiAction.CategoryClicked -> Unit
            is CategoriesUiAction.CategoryLongPressed -> updateState(
                isDeleteMode = true,
                pendingDeleteCategoryId = null,
            )
            is CategoriesUiAction.DeleteCategoryClicked -> state = state.copy(
                isDeleteMode = true,
                pendingDeleteCategoryId = action.categoryId,
            )
            CategoriesUiAction.ConfirmDeleteCategoryClicked -> confirmDeleteCategory()
            CategoriesUiAction.DeleteCategoryDismissed -> state = state.copy(pendingDeleteCategoryId = null)
            CategoriesUiAction.ExitDeleteMode -> updateState(
                isDeleteMode = false,
                pendingDeleteCategoryId = null,
            )
            CategoriesUiAction.RetryClicked -> loadCategories()
            CategoriesUiAction.AddClicked -> state = state.copy(
                isDeleteMode = false,
                pendingDeleteCategoryId = null,
                isAddDialogVisible = true,
                addCategoryName = "",
                selectedIconKey = DefaultCategoryIconKey,
                addCategoryError = null,
            )
            is CategoriesUiAction.AddNameChanged -> state = state.copy(
                addCategoryName = action.name,
                addCategoryError = null,
            )
            is CategoriesUiAction.AddIconSelected -> state = state.copy(selectedIconKey = action.iconKey)
            CategoriesUiAction.AddConfirmed -> addCategory()
            CategoriesUiAction.AddDismissed -> state = state.copy(
                isDeleteMode = false,
                pendingDeleteCategoryId = null,
                isAddDialogVisible = false,
                addCategoryName = "",
                selectedIconKey = DefaultCategoryIconKey,
                addCategoryError = null,
            )
        }
    }

    private fun addCategory() {
        val container = appContainer ?: return
        val trimmedName = state.addCategoryName.trim()

        if (trimmedName.isBlank()) {
            state = state.copy(addCategoryError = "Name is required")
            return
        }
        if (allCategories.any { category -> category.name.equals(trimmedName, ignoreCase = true) }) {
            state = state.copy(addCategoryError = "Category already exists")
            return
        }

        scope.launch {
            state = state.copy(isLoading = true, addCategoryError = null)
            runCatching {
                withContext(ioDispatcher) {
                    container.addCategoryUseCase(
                        CategoryDraft(
                            name = trimmedName,
                            iconKey = state.selectedIconKey,
                            updatedAtEpochMillis = System.currentTimeMillis(),
                        )
                    )
                    val categories = container.getCategoriesUseCase()
                    val movies = container.getMoviesUseCase()
                    categories.toCategoryUiModels(movies)
                }
            }.onSuccess { categories ->
                allCategories = categories
                state = state.copy(
                    isLoading = false,
                    errorMessage = null,
                    isAddDialogVisible = false,
                    addCategoryName = "",
                    selectedIconKey = DefaultCategoryIconKey,
                    addCategoryError = null,
                )
                updateState(isLoading = false, errorMessage = null)
            }.onFailure { throwable ->
                state = state.copy(
                    isLoading = false,
                    addCategoryError = throwable.message ?: "Category could not be saved.",
                )
            }
        }
    }

    private fun confirmDeleteCategory() {
        val categoryId = state.pendingDeleteCategoryId ?: return
        val container = appContainer ?: return

        scope.launch {
            runCatching {
                withContext(ioDispatcher) {
                    container.deleteCategoryUseCase(categoryId)
                    val categories = container.getCategoriesUseCase()
                    val movies = container.getMoviesUseCase()
                    categories.toCategoryUiModels(movies)
                }
            }.onSuccess { categories ->
                allCategories = categories
                val visibleCategories = filterCategories(state.searchQuery)
                state = state.copy(
                    categories = visibleCategories,
                    isDeleteMode = visibleCategories.isNotEmpty(),
                    pendingDeleteCategoryId = null,
                    errorMessage = null,
                )
            }.onFailure { throwable ->
                state = state.copy(
                    pendingDeleteCategoryId = null,
                    errorMessage = throwable.message ?: "Category could not be deleted.",
                )
            }
        }
    }

    private fun updateState(
        searchQuery: String = state.searchQuery,
        isSearchVisible: Boolean = state.isSearchVisible,
        isDeleteMode: Boolean = state.isDeleteMode,
        pendingDeleteCategoryId: String? = state.pendingDeleteCategoryId,
        isLoading: Boolean = state.isLoading,
        errorMessage: String? = state.errorMessage,
    ) {
        state = state.copy(
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

        return allCategories.filter { category ->
            category.name.contains(searchQuery, ignoreCase = true)
        }
    }
}

private fun List<Category>.toCategoryUiModels(
    movies: List<Movie>,
): List<CategoryUiModel> {
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

private fun Int.toMovieCountText(): String {
    return if (this == 1) {
        "1 Movie"
    } else {
        "$this Movies"
    }
}
