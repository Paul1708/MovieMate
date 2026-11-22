package de.fh.erfurt.moviemate.presentation.movies.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryDraft
import de.fh.erfurt.moviemate.movies.domain.model.movie.FetchedMovieData
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.movies.domain.usecase.category.AddCategoryUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.category.GetCategoriesUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.AddMovieUseCase
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.FetchMovieDataUseCase
import de.fh.erfurt.moviemate.presentation.app.throwIfCancellation
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryIconOptions
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
import de.fh.erfurt.moviemate.presentation.movies.components.MovieLookup
import de.fh.erfurt.moviemate.presentation.movies.components.parseOptionalSeasonCount
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
    private val addCategoryUseCase: AddCategoryUseCase,
    fetchMovieDataUseCase: FetchMovieDataUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(AddMovieUiState(isLoading = true))
    val uiState: StateFlow<AddMovieUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AddMovieEffect>()
    val effect: SharedFlow<AddMovieEffect> = _effect.asSharedFlow()

    private val movieLookup = MovieLookup(
        viewModelScope, ioDispatcher, fetchMovieDataUseCase,
        update = { lookup -> updateState { copy(lookup = lookup) } },
        applyDetails = { movie -> updateState { withFetchedMovieData(movie) } },
    )

    fun onAction(action: AddMovieAction) {
        when (action) {
            AddMovieAction.LoadRequested -> loadCategories()
            AddMovieAction.CancelClicked -> emitEffect(AddMovieEffect.NavigateBack)
            AddMovieAction.SaveClicked -> saveMovie()
            AddMovieAction.PosterClicked -> emitEffect(AddMovieEffect.PickPoster)
            is AddMovieAction.PosterSelected -> updateState { copy(posterUrl = action.uri, errorMessage = null) }
            AddMovieAction.FetchMovieDataClicked -> {
                val state = _uiState.value
                updateState { copy(errorMessage = null) }
                movieLookup.search(state.title, state.releaseYear, state.mediaType)
            }
            is AddMovieAction.MovieMatchSelected -> {
                if (_uiState.value.lookup.results?.matches?.any { it.imdbId == action.imdbId } == true) {
                    movieLookup.select(action.imdbId, _uiState.value.mediaType)
                }
            }
            AddMovieAction.MovieMatchesDismissed -> movieLookup.clear()
            is AddMovieAction.AddCategoryNameChanged -> updateState {
                copy(addCategoryName = action.value, addCategoryError = null)
            }
            is AddMovieAction.AddCategoryIconSelected -> updateState {
                copy(addCategoryIconKey = action.iconKey, addCategoryError = null)
            }
            AddMovieAction.AddCategoryConfirmed -> addFetchedCategory()
            AddMovieAction.AddCategoryDismissed -> dismissAddCategoryDialog()
            is AddMovieAction.TitleChanged -> {
                movieLookup.clear()
                updateState { copy(title = action.value, errorMessage = null) }
            }
            is AddMovieAction.DescriptionChanged -> updateState { copy(description = action.value, errorMessage = null) }
            is AddMovieAction.CategoryChanged -> updateState { copy(categoryId = action.value, errorMessage = null) }
            is AddMovieAction.ReleaseYearChanged -> {
                movieLookup.clear()
                updateState { copy(releaseYear = action.value, errorMessage = null) }
            }
            is AddMovieAction.MediaTypeChanged -> {
                if (_uiState.value.mediaType != action.value) {
                    movieLookup.clear()
                    updateState { copy(mediaType = action.value, runtimeMinutes = "", totalSeasons = "", errorMessage = null) }
                }
            }
            is AddMovieAction.TotalSeasonsChanged -> updateState { copy(totalSeasons = action.value, errorMessage = null) }
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
        val state = _uiState.value
        if (!state.canSave) return
        val draft = state.toMovieDraftOrNull() ?: return

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
                        errorMessage = throwable.message ?: "Title could not be saved.",
                    )
                }
            }
        }
    }

    private fun addFetchedCategory() {
        val state = _uiState.value
        val categoryName = state.addCategoryName.trim()
        if (categoryName.isBlank()) {
            updateState { copy(addCategoryError = "Name is required") }
            return
        }

        val existingCategory = state.availableCategories.firstOrNull { category ->
            category.name.matchesCategoryName(categoryName)
        }
        if (existingCategory != null) {
            _uiState.update {
                it.copy(
                    categoryId = existingCategory.id,
                    isAddCategoryDialogVisible = false,
                    addCategoryName = "",
                    addCategoryIconKey = DefaultCategoryIconKey,
                    addCategoryError = null,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingCategory = true, addCategoryError = null) }
            runCatching {
                withContext(ioDispatcher) {
                    addCategoryUseCase(
                        CategoryDraft(
                            name = categoryName,
                            iconKey = state.addCategoryIconKey,
                            updatedAtEpochMillis = System.currentTimeMillis(),
                        )
                    )
                }
            }.onSuccess { category ->
                val categoryUiModel = category.toAddMovieCategoryUiModel()
                _uiState.update {
                    it.copy(
                        categoryId = category.id,
                        availableCategories = (it.availableCategories + categoryUiModel)
                            .distinctBy(CategoryUiModel::id)
                            .sortedBy { item -> item.name.lowercase() },
                        isAddingCategory = false,
                        isAddCategoryDialogVisible = false,
                        addCategoryName = "",
                        addCategoryIconKey = DefaultCategoryIconKey,
                        addCategoryError = null,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                throwable.throwIfCancellation()
                _uiState.update {
                    it.copy(
                        isAddingCategory = false,
                        addCategoryError = throwable.message ?: "Category could not be saved.",
                    )
                }
            }
        }
    }

    private fun dismissAddCategoryDialog() {
        if (_uiState.value.isAddingCategory) return
        _uiState.update {
            it.copy(
                isAddCategoryDialogVisible = false,
                addCategoryName = "",
                addCategoryIconKey = DefaultCategoryIconKey,
                addCategoryError = null,
            )
        }
    }

    private fun AddMovieUiState.withFetchedMovieData(fetchedMovie: FetchedMovieData): AddMovieUiState {
        val matchedCategory = fetchedMovie.genres
            .asSequence()
            .mapNotNull { genre ->
                availableCategories.firstOrNull { category -> category.name.matchesCategoryName(genre) }
            }
            .firstOrNull()
        val suggestedGenre = fetchedMovie.genres.firstOrNull()
        val shouldCreateCategory = categoryId.isBlank() && matchedCategory == null && suggestedGenre != null

        return copy(
            title = fetchedMovie.title,
            description = fetchedMovie.description.ifBlank { description },
            releaseYear = fetchedMovie.releaseYear?.toString() ?: releaseYear,
            posterUrl = fetchedMovie.posterUrl ?: posterUrl,
            runtimeMinutes = fetchedMovie.runtimeMinutes?.toString() ?: runtimeMinutes,
            totalSeasons = fetchedMovie.totalSeasons?.toString() ?: totalSeasons,
            imdbRating = fetchedMovie.imdbRating?.toString() ?: imdbRating,
            categoryId = categoryId.ifBlank { matchedCategory?.id.orEmpty() },
            isAddCategoryDialogVisible = shouldCreateCategory,
            addCategoryName = if (shouldCreateCategory) suggestedGenre.orEmpty() else "",
            addCategoryIconKey = if (shouldCreateCategory) {
                suggestedGenre.orEmpty().suggestedCategoryIconKey()
            } else {
                DefaultCategoryIconKey
            },
            addCategoryError = null,
            errorMessage = null,
        )
    }

    private fun AddMovieUiState.toMovieDraftOrNull(): MovieDraft? {
        return runCatching {
            MovieDraft(
                title = title.trim(),
                description = description.trim(),
                releaseYear = releaseYear.parseOptionalInt("Release year"),
                categoryId = categoryId.trim().ifBlank { null },
                posterUrl = posterUrl?.takeIf(String::isNotBlank),
                runtimeMinutes = if (mediaType == MediaType.MOVIE) runtimeMinutes.parseOptionalInt("Runtime") else null,
                mediaType = mediaType,
                totalSeasons = if (mediaType == MediaType.SERIES) totalSeasons.parseOptionalSeasonCount() else null,
                isWatched = isWatched,
                comment = comment.trim().ifBlank { null },
                userRating = userRating.parseOptionalInt("User rating"),
                imdbRating = imdbRating.parseOptionalDouble("IMDb rating"),
                externalIds = null,
                watchProviders = watchProviders.ifEmpty { null },
                updatedAtEpochMillis = System.currentTimeMillis(),
            )
        }.onFailure { throwable ->
            _uiState.update { it.copy(errorMessage = throwable.message ?: "Title data is invalid.") }
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

private fun String.matchesCategoryName(other: String): Boolean {
    return normalizedCategoryName() == other.normalizedCategoryName()
}

private fun String.suggestedCategoryIconKey(): String {
    return CategoryIconOptions.firstOrNull { option ->
        matchesCategoryName(option.label) || matchesCategoryName(option.key)
    }?.key ?: DefaultCategoryIconKey
}

private fun String.normalizedCategoryName(): String {
    val compactName = lowercase().filter(Char::isLetterOrDigit)
    return when (compactName) {
        "scifi", "sciencefiction" -> "sciencefiction"
        else -> compactName
    }
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
