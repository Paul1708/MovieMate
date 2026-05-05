package de.fh.erfurt.moviemate.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateWatchStatusRequestDto(
    val watched: Boolean
)
