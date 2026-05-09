package de.fh.erfurt.moviemate.movies.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateWatchStatusRequestDto(
    val watched: Boolean
)
