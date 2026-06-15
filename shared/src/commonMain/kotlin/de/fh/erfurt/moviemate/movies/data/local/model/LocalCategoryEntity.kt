package de.fh.erfurt.moviemate.movies.data.local.model

data class LocalCategoryEntity(
    val id: String,
    val name: String,
    val iconKey: String?,
    val updatedAtEpochMillis: Long,
    val version: Long?
)
