package de.fh.erfurt.moviemate.movies.domain.repository.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getCategory(categoryId: String): Category?
    suspend fun addCategory(draft: CategoryDraft): Category
    suspend fun updateCategory(category: Category): Category
    suspend fun deleteCategory(categoryId: String)

}