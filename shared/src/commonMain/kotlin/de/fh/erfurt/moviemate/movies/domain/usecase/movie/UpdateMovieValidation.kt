package de.fh.erfurt.moviemate.movies.domain.usecase.movie

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie

internal fun validateMovieFields(movie: Movie) {
    require(movie.id.isNotBlank()) { "Movie ID cannot be empty" }
    require(movie.title.isNotBlank()) { "Title cannot be empty" }
    movie.runtimeMinutes?.let { rt -> require(rt > 0) { "Runtime must be positive" } }
    validateSeasonCount(movie.mediaType, movie.totalSeasons)
    movie.userRating?.let { ur -> require(ur in 1..10) { "User rating must be between 1 and 10" } }
    movie.imdbRating?.let { ir -> require(ir in 0.0..10.0) { "IMDb rating must be between 0.0 and 10.0" } }
}