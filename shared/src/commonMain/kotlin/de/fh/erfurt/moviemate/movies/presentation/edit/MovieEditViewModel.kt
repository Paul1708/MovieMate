package de.fh.erfurt.moviemate.movies.presentation.edit

import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUseCase

class MovieEditViewModel(
    private val addMovieUseCase: AddMovieUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
) {
    val initialState = MovieEditUiState()

    fun onEvent(event: MovieEditEvent) {
        when (event) {
            is MovieEditEvent.SaveRequested -> Unit
            is MovieEditEvent.AddWatchProviderRequested -> Unit
            is MovieEditEvent.BackClicked -> Unit
            is MovieEditEvent.KeepEditingRequested -> Unit
            is MovieEditEvent.DiscardChangesConfirmed -> Unit
            is MovieEditEvent.RemoveWatchProviderRequested -> Unit
        }
    }
}