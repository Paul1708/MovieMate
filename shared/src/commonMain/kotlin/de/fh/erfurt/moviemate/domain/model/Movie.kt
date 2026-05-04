package de.fh.erfurt.moviemate.domain.model

data class Movie(
    val id: String,
    val title: String,
    val posterUri: String,
    val publishingYear: Int,
    val runtime: Int,
    val rating: Int,
    val isWatched: Boolean,
    val category: Category,
    val description: String,
    val creationTimeEpochMillis: Long,
    val updateTimeEpochMillis: Long
)
