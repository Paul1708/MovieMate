package de.fh.erfurt.moviemate.movies.domain.usecase

import de.fh.erfurt.moviemate.movies.domain.repository.CategoryRepository

class GetCategoryByIdUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: String) = repository.getCategoryById(id)
}