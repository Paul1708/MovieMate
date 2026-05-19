package de.fh.erfurt.moviemate.movies.presentation.detail

import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoryUseCase

class CategoryDetailViewModel(
    private val getCategoryUseCase: GetCategoryUseCase
) {
    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.LoadRequested -> Unit
        }
    }
}