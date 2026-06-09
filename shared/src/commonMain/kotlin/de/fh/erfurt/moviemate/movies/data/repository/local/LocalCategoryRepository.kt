package de.fh.erfurt.moviemate.movies.data.repository.local

import de.fh.erfurt.moviemate.movies.data.local.datasource.CategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.CategoryLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.mapper.local.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalEntity
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
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

    override suspend fun addCategory(draft: CategoryDraft): Category {
        val existingIds = localDataSource.getCategories().map { it.id }
        val storedCategory = localDataSource.insertCategory(
            draft.toLocalEntity(idPolicy.nextId(existingIds))
        )
        return storedCategory.toDomainModel()
    }

    override suspend fun updateCategory(category: Category): Category {
        return localDataSource.updateCategory(category.toLocalEntity()).toDomainModel()
    }

    override suspend fun deleteCategory(categoryId: String) {
        localDataSource.deleteCategory(categoryId)
    }
}