package de.fh.erfurt.moviemate.movies.data.repository.local

import de.fh.erfurt.moviemate.movies.data.local.datasource.CategoryLocalDataSource
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.CategoryLocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.local.idpolicy.LocalIdPolicy
import de.fh.erfurt.moviemate.movies.data.mapper.local.toDomainModel
import de.fh.erfurt.moviemate.movies.data.mapper.local.toLocalEntity
import de.fh.erfurt.moviemate.movies.data.sync.local.LocalDataLock
import de.fh.erfurt.moviemate.movies.data.sync.local.SyncDeletionTracker
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository
import de.fh.erfurt.moviemate.movies.domain.sync.DataChangeNotifier

class LocalCategoryRepository(
    private val localDataSource: CategoryLocalDataSource,
    private val idPolicy: LocalIdPolicy = CategoryLocalIdPolicy,
    private val syncDeletionTracker: SyncDeletionTracker? = null,
    private val dataChangeNotifier: DataChangeNotifier = DataChangeNotifier(),
    private val localDataLock: LocalDataLock = LocalDataLock(),
    private val mutationTimestampProvider: (lastUpdatedEpochMillis: Long) -> Long = { lastUpdated ->
        lastUpdated.nextTimestamp()
    },
) : CategoryRepository {

    override suspend fun getCategories(): List<Category> {
        return localDataSource.getCategories().map { entity -> entity.toDomainModel() }
    }

    override suspend fun getCategory(categoryId: String): Category? {
        return localDataSource.getCategory(categoryId)?.toDomainModel()
    }

    override suspend fun addCategory(draft: CategoryDraft): Category {
        val storedCategory = localDataLock.withLock {
            val existingIds = localDataSource.getCategories().map { it.id }
            localDataSource.insertCategory(
                draft.toLocalEntity(idPolicy.nextId(existingIds)).copy(version = InitialLocalVersion),
            )
        }
        dataChangeNotifier.notifyDataChanged()
        return storedCategory.toDomainModel()
    }

    override suspend fun updateCategory(category: Category): Category {
        val result = localDataLock.withLock {
            val existing = localDataSource.getCategory(category.id)
            val requested = category.toLocalEntity()
            if (existing == null) {
                throw NoSuchElementException("Category not found: ${category.id}")
            } else {
                val contentChanged = requested.copy(
                    updatedAtEpochMillis = existing.updatedAtEpochMillis,
                    version = existing.version,
                ) != existing
                if (!contentChanged) {
                    existing to false
                } else {
                    val resolvedTimestamp = if (requested.updatedAtEpochMillis > existing.updatedAtEpochMillis) {
                        requested.updatedAtEpochMillis
                    } else {
                        mutationTimestampProvider(existing.updatedAtEpochMillis)
                            .coerceAtLeast(existing.updatedAtEpochMillis.nextTimestamp())
                    }
                    val resolvedVersion = maxOf(
                        requested.version ?: 0L,
                        existing.version ?: 0L,
                    ).nextVersion()
                    localDataSource.updateCategory(
                        requested.copy(
                            updatedAtEpochMillis = resolvedTimestamp,
                            version = resolvedVersion,
                        ),
                    ) to true
                }
            }
        }
        if (result.second) dataChangeNotifier.notifyDataChanged()
        return result.first.toDomainModel()
    }

    override suspend fun deleteCategory(categoryId: String) {
        val deleted = localDataLock.withLock {
            val existing = localDataSource.getCategory(categoryId) ?: return@withLock false
            val deletedAt = mutationTimestampProvider(existing.updatedAtEpochMillis)
                .coerceAtLeast(existing.updatedAtEpochMillis.nextTimestamp())
            val deletionTracker = syncDeletionTracker
            if (deletionTracker == null) {
                localDataSource.deleteCategory(categoryId)
            } else {
                deletionTracker.deleteCategoryWithTombstone(categoryId, deletedAt)
            }
            true
        }
        if (deleted) dataChangeNotifier.notifyDataChanged()
    }
}

private fun Long.nextTimestamp(): Long = if (this == Long.MAX_VALUE) this else this + 1L

private fun Long.nextVersion(): Long = if (this == Long.MAX_VALUE) this else this + 1L

private const val InitialLocalVersion = 1L
