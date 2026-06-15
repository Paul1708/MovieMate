package de.fh.erfurt.moviemate.movies.data.local.datasource

import de.fh.erfurt.moviemate.movies.data.local.db.MovieMateDatabase
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.CategoryLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.model.LocalCategoryEntity
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalModel

class SqlDelightCategoryLocalDataSource(
    database: MovieMateDatabase,
    private val idPolicy: LocalIdPolicy = CategoryLocalIdPolicy,
) : CategoryLocalDataSource {

    private val queries = database.categoryEntityQueries

    override suspend fun getCategories(): List<LocalCategoryEntity> {
        return queries.selectAll().executeAsList().map { entity -> entity.toLocalModel() }
    }

    override suspend fun getCategory(categoryId: String): LocalCategoryEntity? {
        return queries.selectById(categoryId).executeAsOneOrNull()?.toLocalModel()
    }

    override suspend fun insertCategory(category: LocalCategoryEntity): LocalCategoryEntity {
        val resolvedCategory = category.resolveIdIfNeeded()
        queries.insertCategory(
            resolvedCategory.id,
            resolvedCategory.name,
            resolvedCategory.iconKey,
            resolvedCategory.updatedAtEpochMillis,
            resolvedCategory.version,
        )
        return resolvedCategory
    }

    override suspend fun updateCategory(category: LocalCategoryEntity): LocalCategoryEntity {
        require(category.id.isNotBlank()) { "Category id must not be blank when updating the local cache." }
        queries.updateCategory(
            category.name,
            category.iconKey,
            category.updatedAtEpochMillis,
            category.version,
            category.id,
        )
        return category
    }

    override suspend fun deleteCategory(categoryId: String) {
        queries.deleteCategory(categoryId)
    }

    private suspend fun LocalCategoryEntity.resolveIdIfNeeded(): LocalCategoryEntity {
        if (id.isNotBlank()) return this
        val existingIds = getCategories().map { it.id }
        return copy(id = idPolicy.nextId(existingIds))
    }
}

