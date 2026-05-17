package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto

interface CategoryRemoteDataSource {
    suspend fun fetchCategories(): List<CategoryDto>

    suspend fun fetchCategory(categoryId: String): CategoryDto?
}