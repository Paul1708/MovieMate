package de.fh.erfurt.moviemate.presentation.home

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

// Datenmodell für die Anzeige eines Films im Homescreen.
// Es enthält Rohwerte für Logik und fertig formatierte Texte für die UI.
data class MovieUiModel(
    val id: String,
    val title: String,
    val rating: Int,
    val ratingText: String,
    val categoryType: CategoryType,
    val categoryText: String,
    val publishingYear: Int,
    val yearText: String,
    val runtimeMinutes: Int,
    val runtimeText: String,
    val posterUri: String?,
    val isWatched: Boolean
)
