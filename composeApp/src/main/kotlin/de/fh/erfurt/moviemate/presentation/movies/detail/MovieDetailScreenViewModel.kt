package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieWatchedStatusUseCase
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiState
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
import de.fh.erfurt.moviemate.presentation.movies.mapper.toDetailUiModel
import de.fh.erfurt.moviemate.presentation.app.throwIfCancellation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailScreenViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMovieUseCase: GetMovieUseCase,
    private val updateMovieWatchedStatusUseCase: UpdateMovieWatchedStatusUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private var currentMovieId: String = ""
    private var currentMovie: Movie? = null
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(MovieDetailScreenState())
    val uiState: StateFlow<MovieDetailScreenState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<MovieDetailScreenEffect>()
    val effect: SharedFlow<MovieDetailScreenEffect> = _effect.asSharedFlow()

    fun onAction(action: MovieDetailAction) {
        when (action) {
            is MovieDetailAction.LoadRequested -> loadMovie(action.movieId)
            MovieDetailAction.RetryClicked -> loadMovie()
            MovieDetailAction.EditClicked -> enterEditMode()
            MovieDetailAction.SaveEditClicked -> saveEditState()
            MovieDetailAction.CancelEditClicked -> cancelEditMode()
            MovieDetailAction.ImageClicked -> emitEffect(MovieDetailScreenEffect.PickPoster)
            is MovieDetailAction.PosterSelected -> updateEditState { copy(posterUrl = action.uri) }
            is MovieDetailAction.WatchedChanged -> {
                if (_uiState.value.mode == MovieDetailMode.Edit) {
                    updateEditState { copy(isWatched = action.isWatched) }
                } else {
                    updateWatched(action.isWatched)
                }
            }
            is MovieDetailAction.TitleChanged -> updateEditState { copy(title = action.value) }
            is MovieDetailAction.DescriptionChanged -> updateEditState { copy(description = action.value) }
            is MovieDetailAction.ReleaseYearChanged -> updateEditState { copy(releaseYear = action.value) }
            is MovieDetailAction.CategoryChanged -> updateEditState { copy(categoryId = action.value) }
            is MovieDetailAction.RuntimeChanged -> updateEditState { copy(runtimeMinutes = action.value) }
            is MovieDetailAction.UserRatingChanged -> updateEditState { copy(userRating = action.value) }
            is MovieDetailAction.ImdbRatingChanged -> updateEditState { copy(imdbRating = action.value) }
            is MovieDetailAction.CommentChanged -> updateEditState { copy(comment = action.value) }
            is MovieDetailAction.WatchProviderInputChanged -> updateEditState { copy(watchProviderInput = action.value) }
            MovieDetailAction.WatchProviderAdded -> updateEditState { withAddedWatchProvider() }
            is MovieDetailAction.WatchProviderRemoved -> updateEditState { withoutWatchProvider(action.value) }
        }
    }

    private fun loadMovie(movieId: String = currentMovieId) {
        currentMovieId = movieId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    detail = it.detail.copy(isLoading = true, errorMessage = null),
                    mode = MovieDetailMode.View,
                    isSaving = false,
                )
            }
            runCatching {
                withContext(ioDispatcher) {
                    val categories = getCategoriesUseCase().map(Category::toDetailCategoryUiModel)
                    categories to getMovieUseCase(movieId)
                }
            }.onSuccess { (categories, movie) ->
                currentMovie = movie
                if (movie == null) {
                    _uiState.value = MovieDetailScreenState(
                        detail = MovieDetailUiState(errorMessage = "Movie not found."),
                        availableCategories = categories,
                    )
                } else {
                    val detailMovie = movie.toDetailUiModel(
                        categories = categories,
                        updatedAtLabel = movie.updatedAtEpochMillis.updatedAtLabel(),
                    )
                    _uiState.value = MovieDetailScreenState(
                        detail = MovieDetailUiState(movie = detailMovie),
                        editState = MovieDetailEditState.from(detailMovie),
                        availableCategories = categories,
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        detail = MovieDetailUiState(errorMessage = throwable.message ?: "Movie could not be loaded."),
                        isSaving = false,
                    )
                }
            }
        }
    }

    private fun enterEditMode() {
        val movie = _uiState.value.detail.movie ?: return
        _uiState.update { it.copy(mode = MovieDetailMode.Edit, editState = MovieDetailEditState.from(movie)) }
    }

    private fun cancelEditMode() {
        val movie = _uiState.value.detail.movie
        _uiState.update {
            it.copy(
                mode = MovieDetailMode.View,
                editState = movie?.let(MovieDetailEditState::from) ?: MovieDetailEditState(),
                isSaving = false,
            )
        }
    }

    private fun updateWatched(isWatched: Boolean) {
        val movieId = currentMovieId
        viewModelScope.launch {
            runCatching {
                withContext(ioDispatcher) { updateMovieWatchedStatusUseCase(movieId, isWatched) }
            }.onSuccess { updatedMovie ->
                currentMovie = updatedMovie
                val state = _uiState.value
                val detailMovie = updatedMovie.toDetailUiModel(
                    categories = state.availableCategories,
                    updatedAtLabel = updatedMovie.updatedAtEpochMillis.updatedAtLabel(),
                )
                _uiState.update {
                    it.copy(
                        detail = MovieDetailUiState(movie = detailMovie),
                        editState = MovieDetailEditState.from(detailMovie),
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        detail = it.detail.copy(
                            errorMessage = throwable.message ?: "Watch status could not be updated."
                        )
                    )
                }
            }
        }
    }

    private fun saveEditState() {
        val state = _uiState.value
        val originalMovie = currentMovie ?: return
        val currentDetailMovie = state.detail.movie ?: return
        val editedMovie = runCatching {
            state.editState.applyTo(currentDetailMovie, state.availableCategories)
        }.onFailure { throwable ->
            _uiState.update {
                it.copy(detail = it.detail.copy(errorMessage = throwable.message ?: "Movie data is invalid."))
            }
        }.getOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, detail = it.detail.copy(errorMessage = null)) }
            runCatching {
                withContext(ioDispatcher) { updateMovieUseCase(editedMovie.toDomainModel(originalMovie)) }
            }.onSuccess { updatedMovie ->
                currentMovie = updatedMovie
                val currentState = _uiState.value
                val detailMovie = updatedMovie.toDetailUiModel(
                    categories = currentState.availableCategories,
                    updatedAtLabel = updatedMovie.updatedAtEpochMillis.updatedAtLabel(),
                )
                _uiState.update {
                    it.copy(
                        detail = MovieDetailUiState(movie = detailMovie),
                        mode = MovieDetailMode.View,
                        editState = MovieDetailEditState.from(detailMovie),
                        isSaving = false,
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        detail = it.detail.copy(errorMessage = throwable.message ?: "Movie could not be saved."),
                        isSaving = false,
                    )
                }
            }
        }
    }

    private fun updateEditState(update: MovieDetailEditState.() -> MovieDetailEditState) {
        _uiState.update { state -> state.copy(editState = state.editState.update()) }
    }

    private fun emitEffect(effect: MovieDetailScreenEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}

data class MovieDetailScreenState(
    val detail: MovieDetailUiState = MovieDetailUiState(isLoading = true),
    val mode: MovieDetailMode = MovieDetailMode.View,
    val editState: MovieDetailEditState = MovieDetailEditState(),
    val availableCategories: List<CategoryUiModel> = emptyList(),
    val isSaving: Boolean = false,
)

sealed interface MovieDetailScreenEffect {
    data object PickPoster : MovieDetailScreenEffect
}

private fun MovieDetailUiModel.toDomainModel(originalMovie: Movie): Movie {
    return originalMovie.copy(
        title = title,
        description = description,
        releaseYear = releaseYear,
        categoryId = categoryId.ifBlank { null },
        posterUrl = posterUrl,
        runtimeMinutes = runtimeMinutes,
        isWatched = isWatched,
        comment = comment,
        userRating = userRating,
        imdbRating = imdbRating,
        watchProviders = watchProviders,
        updatedAtEpochMillis = System.currentTimeMillis(),
    )
}

private fun Category.toDetailCategoryUiModel(): CategoryUiModel {
    return CategoryUiModel(id = id, name = name, iconKey = iconKey ?: DefaultCategoryIconKey)
}

private fun Long.updatedAtLabel(): String = "Updated $this"
