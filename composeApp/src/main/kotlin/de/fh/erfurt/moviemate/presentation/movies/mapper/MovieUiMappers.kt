package de.fh.erfurt.moviemate.presentation.movies.mapper

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.components.displayName
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

/**
 * Mapper zwischen Domain-Movies und den UI-Modellen der Android-Presentation.
 *
 * Die Funktionen sind bewusst nicht im Preview-Paket, damit Preview-Routen und
 * spätere Backend-Routen dieselbe Mapping-Grenze nutzen können.
 */
fun Movie.toHomeUiModel(): MovieUiModel {
    val categoryType = CategoryType.fromString(categoryId)
    val rating = userRating ?: imdbRating?.toInt() ?: 0
    val year = releaseYear ?: 0
    val runtime = runtimeMinutes ?: 0

    return MovieUiModel(
        id = id,
        title = title,
        rating = rating,
        ratingText = "$rating/10",
        categoryType = categoryType,
        categoryText = categoryType.displayName(),
        publishingYear = year,
        yearText = year.takeIf { it > 0 }?.toString().orEmpty(),
        runtimeMinutes = runtime,
        runtimeText = "$runtime Minutes",
        posterUri = posterUrl?.ifBlank { null },
        isWatched = isWatched
    )
}

fun Movie.toDetailUiModel(
    updatedAtLabel: String = "Updated $updatedAtEpochMillis"
): MovieDetailUiModel {
    return MovieDetailUiModel(
        id = id,
        title = title,
        description = description,
        releaseYear = releaseYear,
        categoryName = categoryId.orEmpty(),
        posterUrl = posterUrl?.ifBlank { null },
        runtimeMinutes = runtimeMinutes,
        isWatched = isWatched,
        comment = comment,
        userRating = userRating,
        imdbRating = imdbRating,
        watchProviders = watchProviders.orEmpty(),
        updatedAtLabel = updatedAtLabel
    )
}
