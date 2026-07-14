package de.fh.erfurt.moviemate.movies.data.mapper.remote

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto
import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData

fun OmdbMovieDto.toFetchedMovieData(): FetchedMovieData {
    return FetchedMovieData(
        title = title.orEmpty(),
        description = description.orEmpty(),
        releaseYear = year?.take(4)?.toIntOrNull(),
        posterUrl = poster?.takeIf { it != "N/A" },
        runtimeMinutes = runtime
            ?.replace(" min", "")
            ?.toIntOrNull(),
        imdbRating = imdbRating?.toDoubleOrNull(),
        genres = genre
            .orEmpty()
            .split(",")
            .map(String::trim)
            .filter { value -> value.isNotBlank() && value != "N/A" }
            .distinct(),
    )
}
