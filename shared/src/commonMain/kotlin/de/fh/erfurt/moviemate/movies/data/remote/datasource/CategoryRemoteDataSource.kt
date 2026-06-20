package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateCategoryRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateCategoryRequestDto

interface CategoryRemoteDataSource {
    suspend fun fetchCategories(): List<CategoryDto>
    suspend fun fetchCategory(categoryId: String): CategoryDto?

    suspend fun createCategory(request: CreateCategoryRequestDto): CategoryDto

    suspend fun updateCategory(categoryId: String, request: UpdateCategoryRequestDto): CategoryDto

    suspend fun deleteCategory(categoryId: String)
}