package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class AddCategoryUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(draft: CategoryDraft): Category {
        validateCategoryDraft(draft)

        return categoryRepository.addCategory(
            draft = draft.copy(
                name = draft.name.trim()
            )
        )
    }
}