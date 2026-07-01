package de.fh.erfurt.moviemate.presentation.movies.add

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.MovieDraft
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddMovieRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val stateHolder = remember(appContainer, ioDispatcher, scope) {
        AddMovieRouteStateHolder(
            appContainer = appContainer,
            ioDispatcher = ioDispatcher,
            scope = scope,
            onMovieSaved = onCloseClicked,
        )
    }

    LaunchedEffect(stateHolder) {
        stateHolder.loadCategories()
    }

    val posterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.persistReadPermission(uri)
        stateHolder.onAction(AddMovieAction.PosterSelected(uri.toString()))
    }

    AddMovieScreen(
        state = stateHolder.state,
        onAction = { action ->
            when (action) {
                AddMovieAction.CancelClicked -> onCloseClicked()
                AddMovieAction.PosterClicked -> posterLauncher.launch(ImageMimeTypes)
                else -> stateHolder.onAction(action)
            }
        },
        modifier = modifier,
    )
}

private class AddMovieRouteStateHolder(
    private val appContainer: AppContainer?,
    private val ioDispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
    private val onMovieSaved: () -> Unit,
) {
    var state by mutableStateOf(AddMovieUiState(isLoading = true))
        private set

    fun loadCategories() {
        val container = appContainer
        if (container == null) {
            state = state.copy(
                isLoading = false,
                errorMessage = "Add movie is only available in the running app.",
            )
            return
        }

        scope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            runCatching {
                withContext(ioDispatcher) {
                    container.getCategoriesUseCase().map { category -> category.toUiModel() }
                }
            }.onSuccess { categories ->
                state = state.copy(
                    availableCategories = categories,
                    isLoading = false,
                    errorMessage = null,
                )
            }.onFailure { throwable ->
                state = state.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Categories could not be loaded.",
                )
            }
        }
    }

    fun onAction(action: AddMovieAction) {
        when (action) {
            AddMovieAction.CancelClicked -> Unit
            AddMovieAction.SaveClicked -> saveMovie()
            AddMovieAction.PosterClicked -> Unit
            is AddMovieAction.PosterSelected -> state = state.copy(posterUrl = action.uri, errorMessage = null)
            AddMovieAction.FetchMovieDataClicked -> state = state.copy(
                errorMessage = "Movie data fetch is not connected yet.",
            )
            is AddMovieAction.TitleChanged -> state = state.copy(title = action.value, errorMessage = null)
            is AddMovieAction.DescriptionChanged -> state = state.copy(description = action.value, errorMessage = null)
            is AddMovieAction.CategoryChanged -> state = state.copy(categoryId = action.value, errorMessage = null)
            is AddMovieAction.ReleaseYearChanged -> state = state.copy(releaseYear = action.value, errorMessage = null)
            is AddMovieAction.RuntimeChanged -> state = state.copy(runtimeMinutes = action.value, errorMessage = null)
            is AddMovieAction.UserRatingChanged -> state = state.copy(userRating = action.value, errorMessage = null)
            is AddMovieAction.ImdbRatingChanged -> state = state.copy(imdbRating = action.value, errorMessage = null)
            is AddMovieAction.WatchedChanged -> state = state.copy(isWatched = action.isWatched, errorMessage = null)
            is AddMovieAction.WatchProviderInputChanged -> {
                state = state.copy(watchProviderInput = action.value, errorMessage = null)
            }
            AddMovieAction.WatchProviderAdded -> addWatchProviders()
            is AddMovieAction.WatchProviderRemoved -> {
                state = state.copy(
                    watchProviders = state.watchProviders.filterNot { provider -> provider == action.value },
                    errorMessage = null,
                )
            }
            is AddMovieAction.CommentChanged -> state = state.copy(comment = action.value, errorMessage = null)
        }
    }

    private fun saveMovie() {
        val container = appContainer ?: return
        val draft = state.toMovieDraftOrNull() ?: return

        scope.launch {
            state = state.copy(isSaving = true, errorMessage = null)
            runCatching {
                withContext(ioDispatcher) {
                    container.addMovieUseCase(draft)
                }
            }.onSuccess {
                state = state.copy(isSaving = false, errorMessage = null)
                onMovieSaved()
            }.onFailure { throwable ->
                state = state.copy(
                    isSaving = false,
                    errorMessage = throwable.message ?: "Movie could not be saved.",
                )
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
                posterUrl = posterUrl?.takeIf { url -> url.isNotBlank() },
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
            state = state.copy(errorMessage = throwable.message ?: "Movie data is invalid.")
        }.getOrNull()
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

    private fun addWatchProviders() {
        val additions = state.watchProviderInput
            .split(",")
            .map { provider -> provider.trim() }
            .filter { provider -> provider.isNotBlank() }

        if (additions.isEmpty()) return

        state = state.copy(
            watchProviders = (state.watchProviders + additions).distinctBy { provider -> provider.lowercase() },
            watchProviderInput = "",
            errorMessage = null,
        )
    }
}

private fun Category.toUiModel(): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        name = name,
        iconKey = iconKey ?: DefaultCategoryIconKey,
    )
}

private fun Context.persistReadPermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
}

private val ImageMimeTypes = arrayOf("image/*")
