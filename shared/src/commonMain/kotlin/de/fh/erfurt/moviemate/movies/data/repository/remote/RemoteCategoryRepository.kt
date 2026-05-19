package de.fh.erfurt.moviemate.movies.data.repository.remote

import de.fh.erfurt.moviemate.movies.data.mapper.remote.toDomainModel
import de.fh.erfurt.moviemate.movies.data.remote.datasource.CategoryRemoteDataSource
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.repository.category.CategoryRepository

class RemoteCategoryRepository(
    private val remoteDataSource: CategoryRemoteDataSource,
) : CategoryRepository {
    override suspend fun getCategories(): List<Category> {
        return remoteDataSource.fetchCategories().map { dto -> dto.toDomainModel() }
    }

    override suspend fun getCategory(categoryId: String): Category? {
        return remoteDataSource.fetchCategory(categoryId)?.toDomainModel()
    }
}