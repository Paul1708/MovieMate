package de.fh.erfurt.moviemate.movies.data.mapper.remote

import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

fun CategoryDto.toDomainModel(): Category = Category(
    id = id,
    type = CategoryType.fromString(name),
)
