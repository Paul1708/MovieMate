package de.fh.erfurt.moviemate.movies.domain.model.movie

data class FetchedMovieData(
    val title: String,
    val description: String,
    val releaseYear: Int?,
    val posterUrl: String?,
    val runtimeMinutes: Int?,
    val imdbRating: Double?,
)
