package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.model.Category
import de.fh.erfurt.moviemate.movies.domain.repository.CategoryRepository
import kotlin.time.Clock

class UpdateCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        require(category.id.isNotBlank()) { "The category id cannot be blank." }
        val newCategory = Category(
            id = category.id,
            type = category.type
        )
        repository.updateCategory(newCategory)
    }
}