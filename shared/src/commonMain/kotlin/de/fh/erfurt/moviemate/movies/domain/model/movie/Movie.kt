package de.fh.erfurt.moviemate.movies.domain.model.movie

data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val releaseYear: Int?,
    val categoryId: String?,
    val posterUrl: String?,
    val runtimeMinutes: Int?,
    val isWatched: Boolean = false,
    val comment: String?,
    val userRating: Int?,
    val imdbRating: Double?,
    val externalIds: Map<String, String>?,
    val watchProviders: List<String>?,
    val updatedAtEpochMillis: Long,
    val version: Long? = null,
    val mediaType: MediaType = MediaType.MOVIE,
    val totalSeasons: Int? = null,
)