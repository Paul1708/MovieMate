package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel

fun Movie.toPreviewDetailUiModel(): MovieDetailUiModel {
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
        updatedAtLabel = "Preview update $updatedAtEpochMillis"
    )
}
