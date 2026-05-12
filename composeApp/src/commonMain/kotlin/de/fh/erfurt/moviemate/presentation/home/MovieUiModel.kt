package de.fh.erfurt.moviemate.presentation.home

// Datenmodell für die Anzeige eines Films im Homescreen.
// Es enthält bereits fertig formatierte Texte für die UI.
data class MovieUiModel(
    val id: String,
    val title: String,
    val ratingText: String,
    val categoryText: String,
    val yearText: String,
    val runtimeText: String,
    val posterUri: String?,
    val isWatched: Boolean
)
