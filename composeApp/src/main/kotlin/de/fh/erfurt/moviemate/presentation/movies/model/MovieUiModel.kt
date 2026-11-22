package de.fh.erfurt.moviemate.presentation.movies.model

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
// Datenmodell für die Anzeige eines Films im Homescreen.
data class MovieUiModel(
    val id: String,
    val title: String,
    val rating: Int,
    val ratingText: String,
    val categoryId: String,
    val categoryText: String,
    val categoryIconKey: String?,
    val publishingYear: Int,
    val yearText: String,
    val runtimeMinutes: Int,
    val runtimeText: String,
    val posterUri: String?,
    val isWatched: Boolean,
    val mediaType: MediaType = MediaType.MOVIE,
)
