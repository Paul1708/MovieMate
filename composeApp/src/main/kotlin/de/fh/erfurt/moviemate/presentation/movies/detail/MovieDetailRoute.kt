package de.fh.erfurt.moviemate.presentation.movies.detail

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
import androidx.compose.ui.platform.LocalContext
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.movie.Movie
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiState
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.DefaultCategoryIconKey
import de.fh.erfurt.moviemate.presentation.movies.mapper.toDetailUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MovieDetailRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    movieId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val stateHolder = remember(appContainer, ioDispatcher, scope, movieId) {
        MovieDetailRouteStateHolder(
            appContainer = appContainer,
            ioDispatcher = ioDispatcher,
            scope = scope,
            movieId = movieId,
        )
    }

    LaunchedEffect(stateHolder, movieId) {
        stateHolder.loadMovie(movieId)
    }

    val posterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.persistReadPermission(uri)
        stateHolder.onAction(MovieDetailAction.PosterSelected(uri.toString()))
    }

    MovieDetailScreen(
        state = stateHolder.state,
        mode = stateHolder.mode,
        editState = stateHolder.editState,
        availableCategories = stateHolder.availableCategories,
        onAction = { action ->
            if (action == MovieDetailAction.ImageClicked) {
                posterLauncher.launch(ImageMimeTypes)
            } else {
                stateHolder.onAction(action)
            }
        },
        onBackClicked = onBackClicked,
        onRetryClicked = { stateHolder.loadMovie(movieId) },
        modifier = modifier,
    )
}

private class MovieDetailRouteStateHolder(
    private val appContainer: AppContainer?,
    private val ioDispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
    movieId: String,
) {
    private var currentMovieId = movieId
    private var currentMovie: Movie? = null

    var state by mutableStateOf(MovieDetailUiState(isLoading = true))
        private set

    var mode by mutableStateOf(MovieDetailMode.View)
        private set

    var editState by mutableStateOf(MovieDetailEditState())
        private set

    var availableCategories by mutableStateOf(emptyList<CategoryUiModel>())
        private set

    fun loadMovie(movieId: String = currentMovieId) {
        currentMovieId = movieId
        val container = appContainer
        if (container == null) {
            state = MovieDetailUiState(
                errorMessage = "Movie details are only available in the running app.",
            )
            return
        }

        scope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            mode = MovieDetailMode.View

            runCatching {
                withContext(ioDispatcher) {
                    val categories = container.getCategoriesUseCase().map { category -> category.toUiModel() }
                    val movie = container.getMovieUseCase(movieId)
                    categories to movie
                }
            }.onSuccess { (categories, movie) ->
                availableCategories = categories
                currentMovie = movie

                if (movie == null) {
                    state = MovieDetailUiState(errorMessage = "Movie not found.")
                    editState = MovieDetailEditState()
                } else {
                    val detailMovie = movie.toDetailUiModel(
                        categories = categories,
                        updatedAtLabel = movie.updatedAtEpochMillis.updatedAtLabel(),
                    )
                    state = MovieDetailUiState(movie = detailMovie)
                    editState = MovieDetailEditState.from(detailMovie)
                }
            }.onFailure { throwable ->
                state = MovieDetailUiState(
                    errorMessage = throwable.message ?: "Movie could not be loaded.",
                )
            }
        }
    }

    fun onAction(action: MovieDetailAction) {
        when (action) {
            MovieDetailAction.EditClicked -> enterEditMode()
            MovieDetailAction.SaveEditClicked -> saveEditState()
            MovieDetailAction.CancelEditClicked -> cancelEditMode()
            MovieDetailAction.ImageClicked -> Unit
            is MovieDetailAction.PosterSelected -> editState = editState.copy(posterUrl = action.uri)
            is MovieDetailAction.WatchedChanged -> {
                if (mode == MovieDetailMode.Edit) {
                    editState = editState.copy(isWatched = action.isWatched)
                } else {
                    updateWatched(action.isWatched)
                }
            }
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

    private fun cancelEditMode() {
        mode = MovieDetailMode.View
        resetEditState()
    }

    private fun updateWatched(isWatched: Boolean) {
        val container = appContainer ?: return
        val movieId = currentMovieId

        scope.launch {
            runCatching {
                withContext(ioDispatcher) {
                    container.updateMovieWatchedStatusUseCase(movieId, isWatched)
                }
            }.onSuccess { updatedMovie ->
                currentMovie = updatedMovie
                val detailMovie = updatedMovie.toDetailUiModel(
                    categories = availableCategories,
                    updatedAtLabel = updatedMovie.updatedAtEpochMillis.updatedAtLabel(),
                )
                state = MovieDetailUiState(movie = detailMovie)
                editState = MovieDetailEditState.from(detailMovie)
            }.onFailure { throwable ->
                state = state.copy(errorMessage = throwable.message ?: "Watch status could not be updated.")
            }
        }
    }

    private fun saveEditState() {
        val container = appContainer ?: return
        val originalMovie = currentMovie ?: return
        val currentDetailMovie = state.movie ?: return

        val editedMovie = runCatching {
            editState.applyTo(currentDetailMovie, availableCategories)
        }.onFailure { throwable ->
            state = state.copy(errorMessage = throwable.message ?: "Movie data is invalid.")
        }.getOrNull() ?: return

        scope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            runCatching {
                withContext(ioDispatcher) {
                    container.updateMovieUseCase(editedMovie.toDomainModel(originalMovie))
                }
            }.onSuccess { updatedMovie ->
                currentMovie = updatedMovie
                val detailMovie = updatedMovie.toDetailUiModel(
                    categories = availableCategories,
                    updatedAtLabel = updatedMovie.updatedAtEpochMillis.updatedAtLabel(),
                )
                state = MovieDetailUiState(movie = detailMovie)
                mode = MovieDetailMode.View
                editState = MovieDetailEditState.from(detailMovie)
            }.onFailure { throwable ->
                state = state.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Movie could not be saved.",
                )
            }
        }
    }

    private fun resetEditState() {
        editState = state.movie?.let(MovieDetailEditState::from) ?: MovieDetailEditState()
    }
}

private fun MovieDetailUiModel.toDomainModel(
    originalMovie: Movie,
): Movie {
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

private fun Category.toUiModel(): CategoryUiModel {
    return CategoryUiModel(
        id = id,
        name = name,
        iconKey = iconKey ?: DefaultCategoryIconKey,
    )
}

private fun Long.updatedAtLabel(): String = "Updated $this"

private fun Context.persistReadPermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
}

private val ImageMimeTypes = arrayOf("image/*")
