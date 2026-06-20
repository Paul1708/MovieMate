package de.fh.erfurt.moviemate.movies.presentation.list

data class MovieListItemUiModel(
    val id: String,
    val title: String,
    val description: String,
    val releaseYear: Int? = null,
    val categoryName: String? = null,
    val runtimeMinutes: Int? = null,
    val posterUrl: String? = null,
    val imdbRating: Double? = null,
    val userRating: Int? = null,
    val isWatched: Boolean = false,
    val updatedAtLabel: String
)
