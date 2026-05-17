package de.fh.erfurt.moviemate.movies.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: String,
    val title: String,
    val description: String,
    val releaseYear: Int? = null,
    val categoryId: String? = null,
    val posterUrl: String? = null,
    val runtimeMinutes: Int? = null,
    val isWatched: Boolean = false,
    val comment: String? = null,
    val userRating: Int? = null,
    val imdbRating: Double? = null,
    val externalIds: Map<String, String>? = null,
    val watchProviders: List<String>? = null,
    val updatedAtEpochMillis: Long,
    val version: Long? = null,
)
