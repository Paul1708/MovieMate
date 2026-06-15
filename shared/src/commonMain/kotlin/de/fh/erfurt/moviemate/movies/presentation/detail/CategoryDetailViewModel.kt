package de.fh.erfurt.moviemate.movies.presentation.detail

import de.fh.erfurt.moviemate.movies.domain.usecase.category.DeleteCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoryUseCase

class CategoryDetailViewModel(
    private val getCategoryUseCase: GetCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase

) {
    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.LoadRequested -> Unit
            is CategoryDetailEvent.EditRequested -> Unit
            is CategoryDetailEvent.DeleteRequested -> Unit        }
    }
}