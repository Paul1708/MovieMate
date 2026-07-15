package de.fh.erfurt.moviemate.presentation.movies.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiState
import de.fh.erfurt.moviemate.presentation.app.movieMateViewModelFactory
import de.fh.erfurt.moviemate.presentation.movies.poster.posterDataUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
fun MovieDetailRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    movieId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (appContainer == null) {
        MovieDetailScreen(
            state = MovieDetailUiState(errorMessage = "Movie details are only available in the running app."),
            mode = MovieDetailMode.View,
            editState = MovieDetailEditState(),
            availableCategories = emptyList(),
            onAction = {},
            onBackClicked = onBackClicked,
            onRetryClicked = {},
            modifier = modifier,
        )
        return
    }

    val context = LocalContext.current
    val viewModel: MovieDetailScreenViewModel = viewModel(
        factory = movieMateViewModelFactory {
            MovieDetailScreenViewModel(
                getCategoriesUseCase = appContainer.getCategoriesUseCase,
                getMovieUseCase = appContainer.getMovieUseCase,
                updateMovieWatchedStatusUseCase = appContainer.updateMovieWatchedStatusUseCase,
                updateMovieUseCase = appContainer.updateMovieUseCase,
                ioDispatcher = ioDispatcher,
            )
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val posterScope = rememberCoroutineScope()

    val posterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.persistReadPermission(uri)
        posterScope.launch {
            val portablePoster = runCatching { context.posterDataUri(uri, ioDispatcher) }
                .getOrElse { uri.toString() }
            viewModel.onAction(MovieDetailAction.PosterSelected(portablePoster))
        }
    }

    LaunchedEffect(viewModel, movieId) {
        viewModel.onAction(MovieDetailAction.LoadRequested(movieId))
    }

    LaunchedEffect(viewModel, appContainer, movieId) {
        appContainer.dataChangeRevision.drop(1).collect {
            val currentState = viewModel.uiState.value
            if (currentState.mode == MovieDetailMode.View && !currentState.isSaving) {
                viewModel.onAction(MovieDetailAction.LoadRequested(movieId))
            }
        }
    }

    LaunchedEffect(viewModel, posterLauncher) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MovieDetailScreenEffect.PickPoster -> posterLauncher.launch(ImageMimeTypes)
            }
        }
    }

    MovieDetailScreen(
        state = state.detail,
        mode = state.mode,
        editState = state.editState,
        availableCategories = state.availableCategories,
        onAction = viewModel::onAction,
        onBackClicked = onBackClicked,
        onRetryClicked = { viewModel.onAction(MovieDetailAction.RetryClicked) },
        modifier = modifier,
        isSaving = state.isSaving,
    )
}

private fun Context.persistReadPermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

private val ImageMimeTypes = arrayOf("image/*")
