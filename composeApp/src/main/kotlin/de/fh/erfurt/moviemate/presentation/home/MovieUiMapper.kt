package de.fh.erfurt.moviemate.presentation.home

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.presentation.displayName
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

/**
 * Übersetzt ein Domain-Movie in ein UI-Movie für den Homescreen.
 *
 * Diese Funktion ist das Skelett für die spätere Backend-Anbindung. Wenn der
 * Homescreen echte Filme über `GetMoviesUseCase` bekommt, kann die UI trotzdem
 * mit `MovieUiModel` weiterarbeiten.
 */
fun Movie.toUiModel(): MovieUiModel {
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
