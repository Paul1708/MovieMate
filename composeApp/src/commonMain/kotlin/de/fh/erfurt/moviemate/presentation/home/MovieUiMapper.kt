package de.fh.erfurt.moviemate.presentation.home

import de.fh.erfurt.moviemate.domain.model.Movie
import de.fh.erfurt.moviemate.presentation.displayName

/**
 * Übersetzt ein Domain-Movie in ein UI-Movie für den Homescreen.
 *
 * Diese Funktion ist das Skelett für die spätere Backend-Anbindung. Wenn der
 * Homescreen echte Filme über `GetMoviesUseCase` bekommt, kann die UI trotzdem
 * mit `MovieUiModel` weiterarbeiten.
 */
fun Movie.toUiModel(): MovieUiModel {
    return MovieUiModel(
        id = id,
        title = title,
        rating = rating,
        ratingText = "$rating/10",
        categoryType = category.type,
        categoryText = category.type.displayName(),
        publishingYear = publishingYear,
        yearText = publishingYear.toString(),
        runtimeMinutes = runtime,
        runtimeText = "$runtime Minutes",
        posterUri = posterUri.ifBlank { null },
        isWatched = isWatched
    )
}
