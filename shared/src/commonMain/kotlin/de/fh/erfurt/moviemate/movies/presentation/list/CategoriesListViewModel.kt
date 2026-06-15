package de.fh.erfurt.moviemate.movies.presentation.list

import de.fh.erfurt.moviemate.movies.domain.usecase.category.DeleteCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase

class CategoriesListViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase

) {
    fun onEvent(event: CategoriesListEvent) {
        when (event) {
            CategoriesListEvent.LoadRequested -> Unit
            CategoriesListEvent.AddRequested -> Unit
            is CategoriesListEvent.SelectRequested -> Unit
            is CategoriesListEvent.EditRequested -> Unit
            is CategoriesListEvent.DeleteRequested -> Unit
        }
    }
}