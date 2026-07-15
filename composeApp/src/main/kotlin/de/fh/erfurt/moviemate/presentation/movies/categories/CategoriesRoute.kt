package de.fh.erfurt.moviemate.presentation.movies.categories

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
fun CategoriesRoute(
    appContainer: AppContainer?,
    ioDispatcher: CoroutineDispatcher,
    modifier: Modifier = Modifier,
    onCategorySelected: (String) -> Unit = {},
) {
    if (appContainer == null) {
        CategoriesScreen(
            state = CategoriesUiState(errorMessage = "Categories are only available in the running app."),
            onAction = {},
            modifier = modifier,
        )
        return
    }

    val viewModel: CategoriesViewModel = viewModel(
        factory = movieMateViewModelFactory {
            CategoriesViewModel(
                getCategoriesUseCase = appContainer.getCategoriesUseCase,
                getMoviesUseCase = appContainer.getMoviesUseCase,
                addCategoryUseCase = appContainer.addCategoryUseCase,
                deleteCategoryUseCase = appContainer.deleteCategoryUseCase,
                ioDispatcher = ioDispatcher,
            )
        }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onAction(CategoriesUiAction.LoadRequested)
    }

    LaunchedEffect(viewModel, appContainer) {
        appContainer.dataChangeRevision.drop(1).collect {
            viewModel.onAction(CategoriesUiAction.LoadRequested)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CategoriesEffect.NavigateToCategory -> onCategorySelected(effect.categoryId)
            }
        }
    }

    CategoriesScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}
