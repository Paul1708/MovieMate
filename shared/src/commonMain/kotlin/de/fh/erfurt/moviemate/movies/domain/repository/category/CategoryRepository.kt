package de.fh.erfurt.moviemate.movies.domain.repository.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>

    suspend fun getCategory(categoryId: String): Category?

}