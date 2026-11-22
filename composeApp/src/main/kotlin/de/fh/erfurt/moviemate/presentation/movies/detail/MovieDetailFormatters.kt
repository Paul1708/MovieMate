package de.fh.erfurt.moviemate.presentation.movies.detail

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.components.seasonCountLabel

/**
 * Erstellt die Metadaten-Chips aus dem Detailmodell.
 */
internal fun MovieDetailUiModel.metaItems(): List<MovieDetailMetaItem> = buildList {
    if (categoryName.isNotBlank()) {
        add(MovieDetailMetaItem(MovieDetailMetaIcon.Category(categoryIconKey), "Category", categoryName))
    }
    if (mediaType == MediaType.SERIES) {
        val value = totalSeasons?.let { "Series · ${seasonCountLabel(it)}" } ?: "Series"
        add(MovieDetailMetaItem(icon = null, label = "Type", value = value))
    } else {
        runtimeMinutes?.let { add(MovieDetailMetaItem(MovieDetailMetaIcon.Runtime, "Runtime", "$it min")) }
    }
    releaseYear?.let { add(MovieDetailMetaItem(MovieDetailMetaIcon.Date, "Year", it.toString())) }
}
