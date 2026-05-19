package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class GetCategoryUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(categoryId: String): Category? {
        require(categoryId.isNotBlank()) { "Category ID cannot be empty" }
        return categoryRepository.getCategory(categoryId)
    }
}