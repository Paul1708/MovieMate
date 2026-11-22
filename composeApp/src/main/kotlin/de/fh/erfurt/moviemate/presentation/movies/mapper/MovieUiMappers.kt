package de.fh.erfurt.moviemate.presentation.movies.mapper

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.defaultIconKey
import de.fh.erfurt.moviemate.presentation.movies.components.displayName
import de.fh.erfurt.moviemate.presentation.movies.components.seasonCountLabel
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

/**
 * Mapper between domain movies and Android UI models.
 */
fun Movie.toHomeUiModel(
    categories: List<CategoryUiModel> = emptyList(),
): MovieUiModel {
    val category = categories.firstOrNull { it.id == categoryId }
    val legacyCategoryType = CategoryType.fromString(categoryId)
    val rating = userRating ?: imdbRating?.toInt() ?: 0
    val year = releaseYear ?: 0
    val runtime = runtimeMinutes ?: 0

    return MovieUiModel(
        id = id,
        title = title,
        rating = rating,
        ratingText = "$rating/10",
        categoryId = categoryId.orEmpty(),
        categoryText = category?.name ?: legacyCategoryType.displayName(),
        categoryIconKey = category?.iconKey ?: legacyCategoryType.defaultIconKey(),
        publishingYear = year,
        yearText = year.takeIf { it > 0 }?.toString().orEmpty(),
        runtimeMinutes = runtime,
        runtimeText = if (mediaType == MediaType.SERIES) {
            totalSeasons?.let { "Series · ${seasonCountLabel(it)}" } ?: "Series"
        } else {
            "$runtime Minutes"
        },
        mediaType = mediaType,
        posterUri = posterUrl?.ifBlank { null },
        isWatched = isWatched
    )
}

fun Movie.toDetailUiModel(
    categories: List<CategoryUiModel> = emptyList(),
    updatedAtLabel: String = "Updated $updatedAtEpochMillis"
): MovieDetailUiModel {
    val category = categories.firstOrNull { it.id == categoryId }
    val legacyCategoryType = CategoryType.fromString(categoryId)

    return MovieDetailUiModel(
        id = id,
        title = title,
        description = description,
        categoryId = categoryId.orEmpty(),
        categoryName = category?.name ?: legacyCategoryType.displayName(),
        categoryIconKey = category?.iconKey ?: legacyCategoryType.defaultIconKey(),
        releaseYear = releaseYear,
        posterUrl = posterUrl?.ifBlank { null },
        runtimeMinutes = runtimeMinutes,
        mediaType = mediaType,
        totalSeasons = totalSeasons,
        isWatched = isWatched,
        comment = comment,
        userRating = userRating,
        imdbRating = imdbRating,
        watchProviders = watchProviders.orEmpty(),
        updatedAtLabel = updatedAtLabel
    )
}
