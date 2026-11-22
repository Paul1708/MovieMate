package de.fh.erfurt.moviemate.movies.data.local.model

data class LocalMovieEntity(
    val id: String,
    val title: String,
    val description: String,
    val releaseYear: Long?,
    val categoryId: String?,
    val posterUrl: String?,
    val runtimeMinutes: Long?,
    val isWatched: Long,
    val comment: String?,
    val userRating: Long?,
    val imdbRating: Double?,
    val externalIds: String?,
    val watchProviders: String?,
    val updatedAtEpochMillis: Long,
    val version: Long?,
    val mediaType: String? = null,
    val totalSeasons: Long? = null,
)
