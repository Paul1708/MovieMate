package de.fh.erfurt.moviemate.movies.domain.model.category

data class Category(
    val id: String,
    val name: String,
    val iconKey: String? = null,
    val updatedAtEpochMillis: Long,
    val version: Long? = null,
)
