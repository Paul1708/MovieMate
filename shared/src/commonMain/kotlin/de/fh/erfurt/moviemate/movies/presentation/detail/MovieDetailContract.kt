package de.fh.erfurt.moviemate.movies.presentation.detail


data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movie: MovieDetailUiModel? = null,
    val errorMessage: String? = null,
)

data class MovieDetailUiModel(
    val id: String,
    val title: String,
    val description: String,
    val releaseYear: Int? = null,
    val categoryId: String? = null,
    val posterUrl: String? = null,
    val runtimeMinutes: Int? = null,
    val isWatched: Boolean = false,
    val comment: String? = null,
    val userRating: Int? = null,
    val imdbRating: Double? = null,
    val watchProviders: List<String>? = null,
    val updatedAtLabel: String,
)

sealed interface MovieDetailEvent {
    data class LoadRequested(
        val movieId: String,
    ) : MovieDetailEvent

    data class UpdateWatchedStatusRequested(
        val movieId: String,
        val isWatched: Boolean,
    ) : MovieDetailEvent

    data class UpdateUserRatingRequested(
        val movieId: String,
        val rating: Int?,
    ) : MovieDetailEvent

    data class UpdateCommentRequested(
        val movieId: String,
        val comment: String?,
    ) : MovieDetailEvent
}

sealed interface MovieDetailEffect {
    data class ShowMessage(
        val message: String,
    ) : MovieDetailEffect
}
