package de.fh.erfurt.moviemate.movies.data.mapper.local

import de.fh.erfurt.moviemate.movies.data.local.db.CategoryEntity
import de.fh.erfurt.moviemate.movies.data.local.db.MovieEntity
import de.fh.erfurt.moviemate.movies.data.local.model.LocalCategoryEntity
import de.fh.erfurt.moviemate.movies.data.local.model.LocalMovieEntity
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val localJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

fun Movie.toLocalEntity(): LocalMovieEntity = LocalMovieEntity(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear?.toLong(),
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes?.toLong(),
    isWatched = if (isWatched) 1L else 0L,
    comment = comment,
    userRating = userRating?.toLong(),
    imdbRating = imdbRating,
    externalIds = externalIds.toJsonStringOrNull(),
    watchProviders = watchProviders.toJsonStringOrNull(),
    updatedAtEpochMillis = updatedAtEpochMillis,
    version = version,
)

fun MovieDraft.toLocalEntity(id: String): LocalMovieEntity = LocalMovieEntity(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear?.toLong(),
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes?.toLong(),
    isWatched = if (isWatched) 1L else 0L,
    comment = comment,
    userRating = userRating?.toLong(),
    imdbRating = imdbRating,
    externalIds = externalIds.toJsonStringOrNull(),
    watchProviders = watchProviders.toJsonStringOrNull(),
    updatedAtEpochMillis = updatedAtEpochMillis,
    version = null,
)

fun LocalMovieEntity.toDomainModel(): Movie = Movie(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear?.toInt(),
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes?.toInt(),
    isWatched = isWatched != 0L,
    comment = comment,
    userRating = userRating?.toInt(),
    imdbRating = imdbRating,
    externalIds = externalIds.fromJsonStringOrNull(),
    watchProviders = watchProviders.fromJsonStringOrNull(),
    updatedAtEpochMillis = updatedAtEpochMillis,
    version = version,
)

fun MovieEntity.toLocalModel(): LocalMovieEntity = LocalMovieEntity(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear,
    categoryId = categoryId,
    posterUrl = posterUrl,
    runtimeMinutes = runtimeMinutes,
    isWatched = isWatched,
    comment = comment,
    userRating = userRating,
    imdbRating = imdbRating,
    externalIds = externalIds,
    watchProviders = watchProviders,
    updatedAtEpochMillis = updatedAtEpochMillis,
    version = version,
)

fun LocalCategoryEntity.toDomainModel(): Category = Category(
    id = id,
    type = CategoryType.fromString(type),
)

fun Category.toLocalEntity(): LocalCategoryEntity = LocalCategoryEntity(
    id = id,
    type = type.storageKey,
)

fun CategoryEntity.toLocalModel(): LocalCategoryEntity = LocalCategoryEntity(
    id = id,
    type = type,
)

private fun List<String>?.toJsonStringOrNull(): String? =
    this?.let { localJson.encodeToString(ListSerializer(String.serializer()), it) }

private fun Map<String, String>?.toJsonStringOrNull(): String? =
    this?.let { localJson.encodeToString(MapSerializer(String.serializer(), String.serializer()), it) }

private inline fun <reified T> String?.fromJsonStringOrNull(): T? =
    this?.takeIf { it.isNotBlank() }?.let { localJson.decodeFromString(it) }


