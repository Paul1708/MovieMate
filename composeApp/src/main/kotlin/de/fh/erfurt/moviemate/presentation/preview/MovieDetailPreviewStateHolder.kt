package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailAction
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailEditState
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailMode

class MovieDetailPreviewStateHolder(
    private val sourceMovies: List<Movie> = sampleMovies(),
    movieId: String
) {
    private var currentMovieId = movieId

    var state by mutableStateOf(previewMovieDetailUiState(movieId, sourceMovies))
        private set

    var mode by mutableStateOf(MovieDetailMode.View)
        private set

    var editState by mutableStateOf(state.movie?.let(MovieDetailEditState::from) ?: MovieDetailEditState())
        private set

    fun loadMovie(movieId: String) {
        currentMovieId = movieId
        state = previewMovieDetailUiState(movieId, sourceMovies)
        mode = MovieDetailMode.View
        resetEditState()
    }

    fun onAction(action: MovieDetailAction) {
        when (action) {
            MovieDetailAction.EditClicked -> enterEditMode()
            MovieDetailAction.SaveEditClicked -> saveEditState()
            MovieDetailAction.CancelEditClicked -> cancelEditMode()
            MovieDetailAction.ImageClicked -> Unit
            is MovieDetailAction.WatchedChanged -> updateWatched(action.isWatched)
            is MovieDetailAction.TitleChanged -> editState = editState.copy(title = action.value)
            is MovieDetailAction.DescriptionChanged -> editState = editState.copy(description = action.value)
            is MovieDetailAction.ReleaseYearChanged -> editState = editState.copy(releaseYear = action.value)
            is MovieDetailAction.CategoryChanged -> editState = editState.copy(categoryId = action.value)
            is MovieDetailAction.RuntimeChanged -> editState = editState.copy(runtimeMinutes = action.value)
            is MovieDetailAction.UserRatingChanged -> editState = editState.copy(userRating = action.value)
            is MovieDetailAction.ImdbRatingChanged -> editState = editState.copy(imdbRating = action.value)
            is MovieDetailAction.CommentChanged -> editState = editState.copy(comment = action.value)
            is MovieDetailAction.WatchProviderInputChanged -> {
                editState = editState.copy(watchProviderInput = action.value)
            }
            MovieDetailAction.WatchProviderAdded -> {
                editState = editState.withAddedWatchProvider()
            }
            is MovieDetailAction.WatchProviderRemoved -> {
                editState = editState.withoutWatchProvider(action.value)
            }
        }
    }

    private fun enterEditMode() {
        resetEditState()
        mode = MovieDetailMode.Edit
    }

    private fun saveEditState() {
        val movie = state.movie ?: return
        state = state.copy(movie = editState.applyTo(movie))
        mode = MovieDetailMode.View
        resetEditState()
    }

    private fun cancelEditMode() {
        mode = MovieDetailMode.View
        resetEditState()
    }

    private fun updateWatched(isWatched: Boolean) {
        updateMovie { copy(isWatched = isWatched) }
        editState = editState.copy(isWatched = isWatched)
    }

    private fun resetEditState() {
        editState = state.movie?.let(MovieDetailEditState::from) ?: MovieDetailEditState()
    }

    private fun updateMovie(update: MovieDetailUiModel.() -> MovieDetailUiModel) {
        val movie = state.movie ?: return
        if (movie.id != currentMovieId) return

        state = state.copy(movie = movie.update())
    }
}
