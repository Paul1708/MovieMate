package de.fh.erfurt.moviemate.presentation.movies.detail

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.components.displayName
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import kotlin.math.roundToInt

/**
 * Erstellt die Metadaten-Chips aus dem Detailmodell.
 */
internal fun MovieDetailUiModel.metaItems(): List<MovieDetailMetaItem> {
    return listOf(
        MovieDetailMetaItem(
            icon = MovieMateIcons.category,
            label = "Category",
            value = categoryLabel()
        ),
        MovieDetailMetaItem(
            icon = MovieMateIcons.runtime,
            label = "Runtime",
            value = runtimeMinutes?.let { "$it min" } ?: "Unknown"
        ),
        MovieDetailMetaItem(
            icon = MovieMateIcons.year,
            label = "Year",
            value = releaseYear?.toString() ?: "Unknown"
        ),
        MovieDetailMetaItem(
            icon = MovieMateIcons.watched,
            label = "Status",
            value = if (isWatched) "Watched" else "Watch later"
        )
    )
}

/**
 * Formatiert die Kategorie für die lesende Detailansicht.
 */
private fun MovieDetailUiModel.categoryLabel(): String {
    val rawCategory = categoryName.takeIf { it.isNotBlank() } ?: return "Unknown"
    val categoryType = CategoryType.fromString(rawCategory)

    return if (categoryType == CategoryType.UNKNOWN && rawCategory != CategoryType.UNKNOWN.storageKey) {
        rawCategory
    } else {
        categoryType.displayName()
    }
}

/**
 * Wandelt eine Bewertung von 0 bis 10 in fünf Sterne um.
 */
internal fun Double.starText(): String {
    val filledStars = (coerceIn(0.0, 10.0) / 2.0).roundToInt().coerceIn(0, 5)
    return MovieMateIcons.rating.repeat(filledStars) + MovieMateIcons.ratingEmpty.repeat(5 - filledStars)
}

/**
 * Leere Sterneanzeige für fehlende Bewertungen.
 */
internal fun emptyStarText(): String {
    return MovieMateIcons.ratingEmpty.repeat(5)
}
