package de.fh.erfurt.moviemate.movies.data.local.model

data class LocalCategoryEntity(
    val id: String,
    val name: String,
    val updatedAtEpochMillis: Long,
    val version: Long?
)
