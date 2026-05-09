package de.fh.erfurt.moviemate.movies.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRatingRequestDto(
    val rating_score: Int
)
