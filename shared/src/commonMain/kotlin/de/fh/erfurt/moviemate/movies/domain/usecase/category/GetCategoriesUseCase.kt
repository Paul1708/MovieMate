package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): List<Category> = categoryRepository.getCategories()
}