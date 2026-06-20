package de.fh.erfurt.moviemate.movies.domain.usecase.movie

internal fun validateSearchQuery(query: String) {
    require(query.isNotBlank()) { "Search query cannot be empty" }
}
