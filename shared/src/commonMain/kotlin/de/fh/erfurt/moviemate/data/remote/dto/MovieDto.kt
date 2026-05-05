package de.fh.erfurt.moviemate.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: String,
    val title: String,
    val poster_url: String,
    val release_year: Int,
    val runtime_mins: Int,
    val rating_score: Int,
    val watched: Boolean,
    val category_id: String,
    val desc: String,
    val created_at: Long,
    val updated_at: Long
)
