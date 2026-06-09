package de.fh.erfurt.moviemate.movies.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryRequestDto(
    val name: String,
    val updatedAtEpochMillis: Long
)
