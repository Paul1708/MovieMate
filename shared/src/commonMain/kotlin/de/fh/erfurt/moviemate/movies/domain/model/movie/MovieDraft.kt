package de.fh.erfurt.moviemate.movies.domain.model.movie

data class MovieDraft(
    val title: String,
    val description: String,
    val releaseYear: Int?,
    val categoryId: String?,
    val posterUrl: String?,
    val runtimeMinutes: Int?,
    val isWatched: Boolean = false,
    val comment: String? = null,
    val userRating: Int? = null,
    val imdbRating: Double? = null,
    val externalIds: Map<String, String>? = null,
    val watchProviders: List<String>? = null,
    val updatedAtEpochMillis: Long
    )