package de.fh.erfurt.moviemate.domain.model

data class Movie(

    val id: String,
    val title: String,
    val posterUrl: String,
    val publishingYear: Int,
    val runTime: Int,
    val rating: Int,
    val isWatched: Boolean,
    val description: String,
    val categoryId: String,
    val createdAt: String,
    val updatedAt: String
)
