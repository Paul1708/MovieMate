package de.fh.erfurt.moviemate.movies.domain.model.movie

import kotlinx.serialization.Serializable

@Serializable
enum class MediaType {
    MOVIE,
    SERIES,
}
