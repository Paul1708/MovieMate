package de.fh.erfurt.moviemate.movies.presentation.detail

import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUserRatingUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieWatchedStatusUseCase

class MovieDetailViewModel(
    private val getMovieUseCase: GetMovieUseCase,
    private val updateMovieWatchedStatusUseCase: UpdateMovieWatchedStatusUseCase,
    private val updateMovieUserRatingUseCase: UpdateMovieUserRatingUseCase,
) {

    val initialState = MovieDetailUiState()

    fun onEvent(event: MovieDetailEvent) {
        when (event) {
            is MovieDetailEvent.LoadRequested -> Unit
            is MovieDetailEvent.UpdateCommentRequested -> Unit
            is MovieDetailEvent.UpdateUserRatingRequested -> Unit
            is MovieDetailEvent.UpdateWatchedStatusRequested -> Unit
        }
    }
}