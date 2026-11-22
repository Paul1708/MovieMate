package de.fh.erfurt.moviemate.presentation.movies.detail

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.components.seasonCountLabel
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import kotlin.math.roundToInt

/**
 * Erstellt die Metadaten-Chips aus dem Detailmodell.
 */
internal fun MovieDetailUiModel.metaItems(): List<MovieDetailMetaItem> {
    return listOf(
        MovieDetailMetaItem(
            icon = MovieDetailMetaIcon.Category(categoryIconKey),
            label = "Category",
            value = categoryName.ifBlank { "Unknown" }
        ),
        MovieDetailMetaItem(
            icon = if (mediaType == MediaType.SERIES) null else MovieDetailMetaIcon.Runtime,
            label = if (mediaType == MediaType.SERIES) "Type" else "Runtime",
            value = if (mediaType == MediaType.SERIES) {
                totalSeasons?.let { "Series · ${seasonCountLabel(it)}" } ?: "Series"
            } else {
                runtimeMinutes?.let { "$it min" } ?: "Unknown"
            }
        ),
        MovieDetailMetaItem(
            icon = MovieDetailMetaIcon.Date,
            label = "Year",
            value = releaseYear?.toString() ?: "Unknown"
        )
    )
}

/**
 * Wandelt eine Bewertung von 0 bis 10 in fünf Sterne um.
 */
internal fun Double.starText(): String {
    val filledStars = (coerceIn(0.0, 10.0) / 2.0).roundToInt().coerceIn(0, 5)
    return MovieMateIcons.ratingFilledText.repeat(filledStars) +
        MovieMateIcons.ratingEmptyText.repeat(5 - filledStars)
}

/**
 * Leere Sterneanzeige für fehlende Bewertungen.
 */
internal fun emptyStarText(): String {
    return MovieMateIcons.ratingEmptyText.repeat(5)
}
