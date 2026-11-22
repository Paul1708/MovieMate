package de.fh.erfurt.moviemate.movies.presentation.detail

import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
/** Render state for a movie detail screen. */
data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movie: MovieDetailUiModel? = null,
    val errorMessage: String? = null,
)

/** Presentation model with values already prepared for detail rendering. */
data class MovieDetailUiModel(
    val id: String,
    val title: String,
    val description: String,
    val categoryId: String = "",
    val categoryName: String,
    val categoryIconKey: String? = null,
    val releaseYear: Int? = null,
    val posterUrl: String? = null,
    val runtimeMinutes: Int? = null,
    val mediaType: MediaType = MediaType.MOVIE,
    val totalSeasons: Int? = null,
    val isWatched: Boolean = false,
    val comment: String? = null,
    val userRating: Int? = null,
    val imdbRating: Double? = null,
    val watchProviders: List<String> = emptyList(),
    val updatedAtLabel: String,
)
