package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesUiAction
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoriesUiState
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey

/**
 * Temporary state holder for preview/demo category data.
 */
class CategoriesPreviewStateHolder(
    private val sourceMovies: List<Movie> = sampleMovies(),
    initialCategories: List<CategoryUiModel> = sampleCategories(),
) {
    private var allCategories: List<CategoryUiModel> = previewCategories(sourceMovies, initialCategories)

    var state by mutableStateOf(CategoriesUiState(categories = allCategories))
        private set

    fun onAction(action: CategoriesUiAction) {
        when (action) {
            CategoriesUiAction.SearchClicked -> updateState(isSearchVisible = !state.isSearchVisible)
            is CategoriesUiAction.SearchChanged -> updateState(searchQuery = action.query)
            is CategoriesUiAction.CategoryClicked -> Unit
            is CategoriesUiAction.CategoryLongPressed -> updateState(
                isDeleteMode = true,
                pendingDeleteCategoryId = null
            )
            is CategoriesUiAction.DeleteCategoryClicked -> state = state.copy(
                isDeleteMode = true,
                pendingDeleteCategoryId = action.categoryId
            )
            CategoriesUiAction.ConfirmDeleteCategoryClicked -> confirmDeleteCategory()
            CategoriesUiAction.DeleteCategoryDismissed -> state = state.copy(pendingDeleteCategoryId = null)
            CategoriesUiAction.ExitDeleteMode -> updateState(
                isDeleteMode = false,
                pendingDeleteCategoryId = null
            )
            CategoriesUiAction.RetryClicked -> updateState()
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

    private fun updateState(
        searchQuery: String = state.searchQuery,
        isSearchVisible: Boolean = state.isSearchVisible,
        isDeleteMode: Boolean = state.isDeleteMode,
        pendingDeleteCategoryId: String? = state.pendingDeleteCategoryId,
    ) {
        state = state.copy(
            categories = filterCategories(searchQuery),
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible,
            isDeleteMode = isDeleteMode,
            pendingDeleteCategoryId = pendingDeleteCategoryId,
            isLoading = false,
            errorMessage = null
        )
    }

    private fun filterCategories(searchQuery: String): List<CategoryUiModel> {
        if (searchQuery.isBlank()) return allCategories

        return allCategories.filter { category ->
            category.name.contains(searchQuery, ignoreCase = true)
        }
    }

    private fun addCategory() {
        val trimmedName = state.addCategoryName.trim()
        if (trimmedName.isBlank()) {
            state = state.copy(addCategoryError = "Name is required")
            return
        }
        if (allCategories.any { it.name.equals(trimmedName, ignoreCase = true) }) {
            state = state.copy(addCategoryError = "Category already exists")
            return
        }

        val newCategory = CategoryUiModel(
            id = trimmedName.toCategoryId(allCategories.map { it.id }),
            name = trimmedName,
            iconKey = state.selectedIconKey,
            movieCountText = "0 Movies",
        )
        allCategories = (allCategories + newCategory).sortedBy { it.name.lowercase() }
        state = state.copy(
            categories = filterCategories(state.searchQuery),
            isDeleteMode = false,
            pendingDeleteCategoryId = null,
            isAddDialogVisible = false,
            addCategoryName = "",
            selectedIconKey = DefaultCategoryIconKey,
            addCategoryError = null,
        )
    }

    private fun confirmDeleteCategory() {
        val categoryId = state.pendingDeleteCategoryId ?: return
        deleteCategory(categoryId)
    }

    private fun deleteCategory(categoryId: String) {
        allCategories = allCategories.filterNot { category -> category.id == categoryId }
        val visibleCategories = filterCategories(state.searchQuery)
        state = state.copy(
            categories = visibleCategories,
            isDeleteMode = visibleCategories.isNotEmpty(),
            pendingDeleteCategoryId = null
        )
    }
}

private fun String.toCategoryId(existingIds: List<String>): String {
    val base = lowercase()
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
        .ifBlank { "category" }

    var candidate = base
    var suffix = 2
    while (candidate in existingIds) {
        candidate = "$base-$suffix"
        suffix++
    }
    return candidate
}
