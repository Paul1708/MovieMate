package de.fh.erfurt.moviemate.movies.presentation.list

data class MoviesListUiState(
    val isLoading: Boolean = false,
    val movies: List<MovieListItemUiModel> = emptyList(),
    val errorMessage: String? = null,
)

sealed interface MoviesListEvent {
    data object LoadRequested : MoviesListEvent

    data class AddRequested(
        val title: String,
        val description: String,
        val releaseYear: Int? = null,
        val categoryId: String? = null,
        val posterUrl: String? = null,
        val runtimeMinutes: Int? = null,
    ) : MoviesListEvent

    data class UpdateRequested(
        val movieId: String,
    ) : MoviesListEvent

    data class DeleteRequested(
        val movieId: String,
    ) : MoviesListEvent

    data class SearchRequested(
        val query: String,
    ) : MoviesListEvent

    data class FilterByCategoryRequested(
        val categoryId: String,
    ) : MoviesListEvent

    data class SortRequested(
        val sortBy: String,  // e.g., "releaseYear", "title", "rating"
        val ascending: Boolean = false,
    ) : MoviesListEvent
}

sealed interface MoviesListEffect {
    data class ShowMessage(
        val message: String,
    ) : MoviesListEffect
}
