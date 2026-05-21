package de.fh.erfurt.moviemate.movies.data.repository.local

import de.fh.erfurt.moviemate.movies.data.local.datasource.CategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.CategoryLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.mapper.local.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalEntity
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class LocalCategoryRepository(
    private val localDataSource: CategoryLocalDataSource,
    private val idPolicy: LocalIdPolicy = CategoryLocalIdPolicy,
) : CategoryRepository {

    override suspend fun getCategories(): List<Category> {
        return localDataSource.getCategories().map { entity -> entity.toDomainModel() }
    }

    override suspend fun getCategory(categoryId: String): Category? {
        return localDataSource.getCategory(categoryId)?.toDomainModel()
    }

    suspend fun saveCategory(category: Category): Category {
        val existingIds = localDataSource.getCategories().map { it.id }
        val resolvedCategory = if (category.id.isBlank()) {
            category.copy(id = idPolicy.nextId(existingIds))
        } else {
            category
        }

        return localDataSource.insertCategory(resolvedCategory.toLocalEntity()).toDomainModel()
    }

    suspend fun saveCategories(categories: List<Category>): List<Category> {
        return categories.map { category -> saveCategory(category) }
    }
}