package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.model.Category
import de.fh.erfurt.moviemate.movies.domain.model.CategoryType
import de.fh.erfurt.moviemate.movies.domain.repository.CategoryRepository
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class AddCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(
        categoryType: CategoryType
    ) {
        val generatedId = Uuid.random().toString()
        val newCategory = Category(
            id = generatedId,
            type = categoryType
        )
        repository.addCategory(newCategory)
    }
}
