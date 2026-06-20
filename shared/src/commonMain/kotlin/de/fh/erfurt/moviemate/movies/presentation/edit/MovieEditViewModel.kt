package de.fh.erfurt.moviemate.movies.presentation.edit

import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.GetMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.UpdateMovieUseCase
import de.fh.erfurt.moviemate.movies.presentation.mapper.toListItemUiModel
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

class MovieEditViewModel(
    private val addMovieUseCase: AddMovieUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
    private val getMovieUseCase: GetMovieUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,

    ) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _uiState = MutableStateFlow(MovieEditUiState())
    val uiState: StateFlow<MovieEditUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<MovieEditEffect>()
    val effect: SharedFlow<MovieEditEffect> = _effect.asSharedFlow()

    fun onEvent(event: MovieEditEvent) {
        when (event) {
            is MovieEditEvent.LoadRequested -> loadInitialData(event.movieId)
            is MovieEditEvent.SaveRequested -> saveMovie(event)
            is MovieEditEvent.AddWatchProviderRequested -> {
                val updated = _uiState.value.currentWatchProviders + event.provider
                _uiState.update { it.copy(currentWatchProviders = updated.distinct()) }
            }
            is MovieEditEvent.RemoveWatchProviderRequested ->  {
                val updated = _uiState.value.currentWatchProviders - event.provider
                _uiState.update { it.copy(currentWatchProviders = updated) }
            }
            is MovieEditEvent.BackClicked -> _uiState.update { it.copy(showDiscardDialog = true) }
            is MovieEditEvent.KeepEditingRequested -> _uiState.update { it.copy(showDiscardDialog = false) }
            is MovieEditEvent.DiscardChangesConfirmed ->  {
                _uiState.update { it.copy(showDiscardDialog = false) }
                coroutineScope.launch { _effect.emit(MovieEditEffect.NavigateBack) }
            }
        }
    }

    private fun loadInitialData(movieId: String?) {
        coroutineScope.launch {
            _uiState.update { it.copy(isLoading = true, initialMovieId = movieId) }
            try {
                val categories = getCategoriesUseCase().map { it.toListItemUiModel() }

                if (movieId != null) {
                    val movie = getMovieUseCase(movieId)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableCategories = categories,
                            currentWatchProviders = movie?.watchProviders ?: emptyList()
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, availableCategories = categories) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun saveMovie(event: MovieEditEvent.SaveRequested) {
        coroutineScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val currentId = _uiState.value.initialMovieId
                val selectedCategoryId = resolveCategoryId(event.categoryName)
                if (currentId != null) {
                    val existing = getMovieUseCase(currentId) ?: throw IllegalStateException("The movie does not exist.")
                    updateMovieUseCase(
                        existing.copy(
                            title = event.title,
                            description = event.description,
                            releaseYear = event.releaseYear,
                            categoryId = selectedCategoryId,
                            posterUrl = event.posterUrl,
                            runtimeMinutes = event.runtimeMinutes,
                            userRating = event.userRating,
                            imdbRating = event.imdbRating,
                            isWatched = event.isWatched,
                            watchProviders = _uiState.value.currentWatchProviders,
                            comment = event.comment,
                            updatedAtEpochMillis = event.updatedAtEpochMillis
                        )
                    )
                } else {
                    addMovieUseCase(
                        MovieDraft(
                            title = event.title,
                            description = event.description,
                            releaseYear = event.releaseYear,
                            categoryId = selectedCategoryId,
                            posterUrl = event.posterUrl,
                            runtimeMinutes = event.runtimeMinutes,
                            userRating = event.userRating,
                            imdbRating = event.imdbRating,
                            isWatched = event.isWatched,
                            watchProviders = _uiState.value.currentWatchProviders,
                            comment = event.comment,
                            updatedAtEpochMillis = event.updatedAtEpochMillis
                        )
                    )
                }

                _uiState.update { it.copy(isSaving = false) }
                _effect.emit(MovieEditEffect.SavedSuccess)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
                _effect.emit(MovieEditEffect.ShowMessage(e.message ?: "Failed to save."))
            }
        }
    }

    private fun resolveCategoryId(categoryName: String?): String? {
        val trimmed = categoryName?.trim().orEmpty()
        if (trimmed.isBlank()) return null

        return _uiState.value.availableCategories.firstOrNull { category ->
            category.id == trimmed || category.name == trimmed
        }?.id ?: trimmed
    }
}