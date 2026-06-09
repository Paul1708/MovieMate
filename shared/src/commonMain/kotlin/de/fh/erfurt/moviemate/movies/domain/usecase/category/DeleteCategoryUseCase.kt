package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class DeleteCategoryUseCase(
        private val categoryRepository: CategoryRepository,
    ) {
    suspend operator fun invoke(categoryId: String) {
        require(categoryId.isNotBlank()) { "Category ID cannot be empty" }
        categoryRepository.deleteCategory(categoryId)
    }
}