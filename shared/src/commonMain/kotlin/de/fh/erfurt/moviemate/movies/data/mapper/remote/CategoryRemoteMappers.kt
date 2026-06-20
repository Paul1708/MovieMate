package de.fh.erfurt.moviemate.movies.data.mapper.remote

import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateCategoryRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateCategoryRequestDto
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft

fun CategoryDto.toDomainModel(): Category = Category(
    id = id,
    name = name,
    iconKey = iconKey,
    updatedAtEpochMillis = updatedAtEpochMillis,
    version = version,
)

fun CategoryDraft.toCreateRequestDto(): CreateCategoryRequestDto = CreateCategoryRequestDto(
    name = name,
    iconKey = iconKey,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

fun Category.toUpdateRequestDto(): UpdateCategoryRequestDto = UpdateCategoryRequestDto(
    name = name,
    iconKey = iconKey,
    updatedAtEpochMillis = updatedAtEpochMillis,
)
