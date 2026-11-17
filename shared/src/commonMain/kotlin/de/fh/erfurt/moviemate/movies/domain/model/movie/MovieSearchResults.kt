package de.fh.erfurt.moviemate.movies.domain.model.movie

data class MovieSearchMatch(
    val imdbId: String,
    val title: String,
    val year: String,
    val posterUrl: String?,
)

data class MovieSearchResults(
    val matches: List<MovieSearchMatch>,
    val totalResults: Int = matches.size,
) {
    val singleMatch: MovieSearchMatch?
        get() = matches.singleOrNull()?.takeIf { totalResults == 1 }
}
