package de.fh.erfurt.moviemate.movies.presentation.list

import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase

class CategoriesListViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase
) {
    fun onEvent(event: CategoriesListEvent) {
        when (event) {
            CategoriesListEvent.LoadRequested -> Unit
            is CategoriesListEvent.SelectRequested -> Unit
        }
    }
}