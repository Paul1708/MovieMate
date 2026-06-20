package de.fh.erfurt.moviemate.movies.data.local.datasource

import de.fh.erfurt.moviemate.movies.data.local.model.LocalCategoryEntity

interface CategoryLocalDataSource {
    suspend fun getCategories(): List<LocalCategoryEntity>

    suspend fun getCategory(categoryId: String): LocalCategoryEntity?

    suspend fun insertCategory(category: LocalCategoryEntity): LocalCategoryEntity

    suspend fun updateCategory(category: LocalCategoryEntity): LocalCategoryEntity

    suspend fun deleteCategory(categoryId: String)
}

