package de.fh.erfurt.moviemate.movies.presentation.detail

import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.DeleteMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUserRatingUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieWatchedStatusUseCase
import de.fh.erfurt.moviemate.movies.presentation.mapper.toDetailUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val getMovieUseCase: GetMovieUseCase,
    private val updateMovieWatchedStatusUseCase: UpdateMovieWatchedStatusUseCase,
    private val updateMovieUserRatingUseCase: UpdateMovieUserRatingUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    ) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<MovieDetailEffect>()
    val effect: SharedFlow<MovieDetailEffect> = _effect.asSharedFlow()

    fun onEvent(event: MovieDetailEvent) {
        when (event) {
            is MovieDetailEvent.LoadRequested -> loadMovie(event.movieId)
            is MovieDetailEvent.UpdateUserRatingRequested -> updateRating(event.movieId, event.rating)
            is MovieDetailEvent.UpdateWatchedStatusRequested -> updateWatchedStatus(event.movieId, event.isWatched)
            is MovieDetailEvent.EditRequested -> {
                coroutineScope.launch { _effect.emit(MovieDetailEffect.NavigateToEdit(event.movieId)) }
            }
            is MovieDetailEvent.DeleteRequested -> deleteMovie(event.movieId)
        }
    }

    private fun loadMovie(movieId: String) {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val movie = getMovieUseCase(movieId)
                if (movie != null) {
                    val categoryName = movie.categoryId?.let { getCategoryUseCase(it)?.name }
                    _uiState.update { it.copy(isLoading = false, movie = movie.toDetailUiModel(categoryName)) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "The Movie does not exist." ) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun updateRating(movieId: String, rating: Int?) {
        coroutineScope.launch {
            try {
                updateMovieUserRatingUseCase(movieId, rating)
                loadMovie(movieId)
            } catch (e: Exception) {
                _effect.emit(MovieDetailEffect.ShowMessage(e.message ?: "Failed to update the rating."))
            }
        }
    }

    private fun updateWatchedStatus(movieId: String, isWatched: Boolean) {
        coroutineScope.launch {
            try {
                updateMovieWatchedStatusUseCase(movieId, isWatched)
                loadMovie(movieId)
            } catch (e: Exception) {
                _effect.emit(MovieDetailEffect.ShowMessage(e.message ?: "Failed to update the status."))
            }
        }
    }

    private fun deleteMovie(movieId: String) {
        coroutineScope.launch {
            try {
                deleteMovieUseCase(movieId)
                _effect.emit(MovieDetailEffect.ShowMessage("The movie was successfully deleted."))
            } catch (e: Exception) {
                _effect.emit(MovieDetailEffect.ShowMessage(e.message ?: "Failed to delete."))
            }
        }
    }
}