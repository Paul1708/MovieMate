package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.presentation.app.movieMateViewModelFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.drop

@Composable
fun HomeRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    modifier: Modifier = Modifier,
    initialCategoryId: String? = null,
    onMovieSelected: (String) -> Unit = {},
    onAddClicked: () -> Unit = {},
) {
    if (appContainer == null) {
        HomeScreen(
            state = HomeUiState(errorMessage = "Titles are only available in the running app."),
            onAction = {},
            modifier = modifier,
        )
        return
    }

    val viewModel: HomeViewModel = viewModel(
        factory = movieMateViewModelFactory {
            HomeViewModel(
                getCategoriesUseCase = appContainer.getCategoriesUseCase,
                getMoviesUseCase = appContainer.getMoviesUseCase,
                deleteMovieUseCase = appContainer.deleteMovieUseCase,
                ioDispatcher = ioDispatcher,
            )
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, initialCategoryId) {
        viewModel.onAction(HomeUiAction.LoadRequested(initialCategoryId))
    }

    LaunchedEffect(viewModel, appContainer) {
        appContainer.dataChangeRevision.drop(1).collect {
            viewModel.onAction(
                HomeUiAction.LoadRequested(viewModel.uiState.value.selectedCategoryId)
            )
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> onMovieSelected(effect.movieId)
                HomeEffect.NavigateToAdd -> onAddClicked()
            }
        }
    }

    HomeScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}
