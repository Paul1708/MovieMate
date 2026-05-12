package de.fh.erfurt.moviemate.presentation.home

import de.fh.erfurt.moviemate.domain.model.Movie

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
        ratingText = "$rating/10",
        categoryText = category.type.displayName(),
        yearText = publishingYear.toString(),
        runtimeText = "$runtime Minutes",
        posterUri = posterUri.ifBlank { null },
        isWatched = isWatched
    )
}
