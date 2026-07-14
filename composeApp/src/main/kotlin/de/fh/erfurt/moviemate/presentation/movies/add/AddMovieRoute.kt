package de.fh.erfurt.moviemate.presentation.movies.add

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.presentation.app.movieMateViewModelFactory
import kotlinx.coroutines.CoroutineDispatcher

@Composable
fun AddMovieRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {},
) {
    if (appContainer == null) {
        AddMovieScreen(
            state = AddMovieUiState(errorMessage = "Add movie is only available in the running app."),
            onAction = { action -> if (action == AddMovieAction.CancelClicked) onCloseClicked() },
            modifier = modifier,
        )
        return
    }

    val context = LocalContext.current
    val viewModel: AddMovieViewModel = viewModel(
        factory = movieMateViewModelFactory {
            AddMovieViewModel(
                getCategoriesUseCase = appContainer.getCategoriesUseCase,
                addMovieUseCase = appContainer.addMovieUseCase,
                addCategoryUseCase = appContainer.addCategoryUseCase,
                fetchMovieDataUseCase = appContainer.fetchMovieDataUseCase,
                ioDispatcher = ioDispatcher,
            )
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val posterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.persistReadPermission(uri)
        viewModel.onAction(AddMovieAction.PosterSelected(uri.toString()))
    }

    LaunchedEffect(viewModel) {
        viewModel.onAction(AddMovieAction.LoadRequested)
    }

    LaunchedEffect(viewModel, posterLauncher) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddMovieEffect.PickPoster -> posterLauncher.launch(ImageMimeTypes)
                AddMovieEffect.NavigateBack -> onCloseClicked()
            }
        }
    }

    AddMovieScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

private fun Context.persistReadPermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

private val ImageMimeTypes = arrayOf("image/*")
