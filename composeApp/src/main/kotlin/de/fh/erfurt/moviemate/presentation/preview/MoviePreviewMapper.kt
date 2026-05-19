package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.presentation.movies.components.displayName
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel

/**
 * Übersetzt ein Domain-Movie in ein UI-Movie für den Homescreen.
 *
 * Diese Funktion ist das Skelett für die spätere Backend-Anbindung. Wenn der
 * Homescreen echte Filme über `GetMoviesUseCase` bekommt, kann die UI trotzdem
 * mit `MovieUiModel` weiterarbeiten.
 */
fun Movie.toPreviewUiModel(): MovieUiModel {
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
