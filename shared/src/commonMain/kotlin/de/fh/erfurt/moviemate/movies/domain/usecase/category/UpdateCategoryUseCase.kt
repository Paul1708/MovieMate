package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class UpdateCategoryUseCase(
    private val categoryRepository: CategoryRepository,
    ) {
    suspend operator fun invoke(category: Category): Category {
        validateCategory(category)

        return categoryRepository.updateCategory(
            category = category.copy(
                name = category.name.trim()
            )
        )
    }
}