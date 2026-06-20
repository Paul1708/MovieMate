package de.fh.erfurt.moviemate.movies.domain.repository

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class FakeCategoryRepository(
    initialCategories: List<Category> = emptyList()
) : CategoryRepository {
    private val storedCategories = initialCategories.toMutableList()

    override suspend fun getCategories(): List<Category> = storedCategories.toList()

    override suspend fun getCategory(categoryId: String): Category? {
        return storedCategories.find { it.id == categoryId }
    }

    override suspend fun addCategory(draft: CategoryDraft): Category {
        val nextId = (storedCategories.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: storedCategories.size) + 1
        val category = Category(
            id = nextId.toString(),
            name = draft.name,
            iconKey = draft.iconKey,
            updatedAtEpochMillis = draft.updatedAtEpochMillis,
        )
        storedCategories += category
        return category
    }

    override suspend fun updateCategory(category: Category): Category {
        val index = storedCategories.indexOfFirst { it.id == category.id }
        if (index >= 0) {
            storedCategories[index] = category
        }
        return category
    }

    override suspend fun deleteCategory(categoryId: String) {
        storedCategories.removeAll { it.id == categoryId }
    }

}
