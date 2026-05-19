package de.fh.erfurt.moviemate.movies.domain.repository

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class FakeCategoryRepository(
    initialCategories: List<Category> = emptyList()
): CategoryRepository
{
    private val storedCategories = initialCategories.toMutableList()

    override suspend fun getCategories(): List<Category> = storedCategories.toList()

    override suspend fun getCategory(categoryId: String): Category? {
        return storedCategories.find { it.id == categoryId }
    }

}