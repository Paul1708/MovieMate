package de.fh.erfurt.moviemate.presentation.preview

import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiState
import de.fh.erfurt.moviemate.presentation.movies.mapper.toDetailUiModel

fun previewMovieDetailUiState(
    movieId: String = sampleMovies().first().id,
    movies: List<Movie> = sampleMovies()
): MovieDetailUiState {
    val movie = movies.firstOrNull { it.id == movieId }

    return if (movie == null) {
        MovieDetailUiState(errorMessage = "Movie could not be loaded.")
    } else {
        MovieDetailUiState(
            movie = movie.toDetailUiModel(
                updatedAtLabel = "Preview update ${movie.updatedAtEpochMillis}"
            )
        )
    }
}
