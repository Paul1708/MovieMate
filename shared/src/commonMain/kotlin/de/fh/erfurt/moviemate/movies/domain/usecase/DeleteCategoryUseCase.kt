package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.repository.CategoryRepository

class DeleteCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: String) {
        require(id.isNotBlank()) { "The category id cannot be blank." }
        repository.deleteCategory(id)
    }
}