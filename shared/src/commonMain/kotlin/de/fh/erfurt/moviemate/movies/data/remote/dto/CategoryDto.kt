package de.fh.erfurt.moviemate.movies.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val iconKey: String? = null,
    val updatedAtEpochMillis: Long,
    val version: Long? = null,
)
