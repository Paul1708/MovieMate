package de.fh.erfurt.moviemate.presentation.movies.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
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

class AddMovieViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addMovieUseCase: AddMovieUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(AddMovieUiState(isLoading = true))
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AddMovieEffect>()
    val effect: SharedFlow<AddMovieEffect> = _effect.asSharedFlow()

    fun onAction(action: AddMovieAction) {
        when (action) {
            AddMovieAction.LoadRequested -> loadCategories()
            AddMovieAction.CancelClicked -> emitEffect(AddMovieEffect.NavigateBack)
            AddMovieAction.SaveClicked -> saveMovie()
            AddMovieAction.PosterClicked -> emitEffect(AddMovieEffect.PickPoster)
            is AddMovieAction.PosterSelected -> updateState { copy(posterUrl = action.uri, errorMessage = null) }
            AddMovieAction.FetchMovieDataClicked -> updateState {
                copy(errorMessage = "Movie data fetch is not connected yet.")
            }
            is AddMovieAction.TitleChanged -> updateState { copy(title = action.value, errorMessage = null) }
            is AddMovieAction.DescriptionChanged -> updateState { copy(description = action.value, errorMessage = null) }
            is AddMovieAction.CategoryChanged -> updateState { copy(categoryId = action.value, errorMessage = null) }
            is AddMovieAction.ReleaseYearChanged -> updateState { copy(releaseYear = action.value, errorMessage = null) }
            is AddMovieAction.RuntimeChanged -> updateState { copy(runtimeMinutes = action.value, errorMessage = null) }
            is AddMovieAction.UserRatingChanged -> updateState { copy(userRating = action.value, errorMessage = null) }
            is AddMovieAction.ImdbRatingChanged -> updateState { copy(imdbRating = action.value, errorMessage = null) }
            is AddMovieAction.WatchedChanged -> updateState { copy(isWatched = action.isWatched, errorMessage = null) }
            is AddMovieAction.WatchProviderInputChanged -> updateState {
                copy(watchProviderInput = action.value, errorMessage = null)
            }
            AddMovieAction.WatchProviderAdded -> addWatchProviders()
            is AddMovieAction.WatchProviderRemoved -> updateState {
                copy(
                    watchProviders = watchProviders.filterNot { provider -> provider == action.value },
                    errorMessage = null,
                )
            }
            is AddMovieAction.CommentChanged -> updateState { copy(comment = action.value, errorMessage = null) }
        }
    }

    private fun loadCategories() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                withContext(ioDispatcher) {
                    getCategoriesUseCase().map(Category::toAddMovieCategoryUiModel)
                }
            }.onSuccess { categories ->
                _uiState.update {
                    it.copy(
                        availableCategories = categories,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Categories could not be loaded.",
                    )
                }
            }
        }
    }

    private fun saveMovie() {
        val draft = _uiState.value.toMovieDraftOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                withContext(ioDispatcher) { addMovieUseCase(draft) }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, errorMessage = null) }
                _effect.emit(AddMovieEffect.NavigateBack)
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Movie could not be saved.",
                    )
                }
            }
        }
    }

    private fun AddMovieUiState.toMovieDraftOrNull(): MovieDraft? {
        return runCatching {
            MovieDraft(
                title = title.trim(),
                description = description.trim(),
                releaseYear = releaseYear.parseOptionalInt("Release year"),
                categoryId = categoryId.trim().ifBlank { null },
                posterUrl = posterUrl?.takeIf(String::isNotBlank),
                runtimeMinutes = runtimeMinutes.parseOptionalInt("Runtime"),
                isWatched = isWatched,
                comment = comment.trim().ifBlank { null },
                userRating = userRating.parseOptionalInt("User rating"),
                imdbRating = imdbRating.parseOptionalDouble("IMDb rating"),
                externalIds = null,
                watchProviders = watchProviders.ifEmpty { null },
                updatedAtEpochMillis = System.currentTimeMillis(),
            )
        }.onFailure { throwable ->
            _uiState.update { it.copy(errorMessage = throwable.message ?: "Movie data is invalid.") }
        }.getOrNull()
    }

    private fun addWatchProviders() {
        val additions = _uiState.value.watchProviderInput
            .split(",")
            .map(String::trim)
            .filter(String::isNotBlank)
        if (additions.isEmpty()) return

        updateState {
            copy(
                watchProviders = (watchProviders + additions).distinctBy { provider -> provider.lowercase() },
                watchProviderInput = "",
                errorMessage = null,
            )
        }
    }

    private fun updateState(update: AddMovieUiState.() -> AddMovieUiState) {
        _uiState.update { state -> state.update() }
    }

    private fun emitEffect(effect: AddMovieEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}

sealed interface AddMovieEffect {
    data object PickPoster : AddMovieEffect
    data object NavigateBack : AddMovieEffect
}

private fun Category.toAddMovieCategoryUiModel(): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        name = name,
        iconKey = iconKey ?: DefaultCategoryIconKey,
    )
}

private fun String.parseOptionalInt(fieldName: String): Int? {
    val trimmed = trim()
    if (trimmed.isBlank()) return null
    return trimmed.toIntOrNull() ?: throw IllegalArgumentException("$fieldName must be a number.")
}

private fun String.parseOptionalDouble(fieldName: String): Double? {
    val trimmed = trim()
    if (trimmed.isBlank()) return null
    return trimmed.toDoubleOrNull() ?: throw IllegalArgumentException("$fieldName must be a number.")
}
