package de.fh.erfurt.moviemate.movies.presentation.mapper

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.presentation.detail.CategoryDetailUiModel
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.movies.presentation.list.CategoryListItemUiModel
import de.fh.erfurt.moviemate.movies.presentation.list.MovieListItemUiModel


fun Category.toListItemUiModel(): CategoryListItemUiModel = CategoryListItemUiModel(
    id = id,
    name = name
)

fun Category.toDetailUiModel(): CategoryDetailUiModel = CategoryDetailUiModel(
    id = id,
    name = name
)

fun Movie.toListItemUiModel(categoryName: String?): MovieListItemUiModel = MovieListItemUiModel(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear,
    categoryName = categoryName,
    runtimeMinutes = runtimeMinutes,
    posterUrl = posterUrl,
    imdbRating = imdbRating,
    userRating = userRating,
    isWatched = isWatched,
    updatedAtLabel = updatedAtEpochMillis.toString()
)

fun Movie.toDetailUiModel(categoryName: String?): MovieDetailUiModel = MovieDetailUiModel(
    id = id,
    title = title,
    description = description,
    releaseYear = releaseYear,
    categoryName = categoryName ?: "Unbekannt",
    runtimeMinutes = runtimeMinutes,
    posterUrl = posterUrl,
    imdbRating = imdbRating,
    userRating = userRating,
    isWatched = isWatched,
    comment = comment,
    watchProviders = watchProviders ?: emptyList(),
    updatedAtLabel = updatedAtEpochMillis.toString()
)