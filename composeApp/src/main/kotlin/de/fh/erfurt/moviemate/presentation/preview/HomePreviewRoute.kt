package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.movies.home.HomeScreen
import de.fh.erfurt.moviemate.presentation.movies.home.HomeUiAction

/**
 * Preview-Route für den Homescreen, bis echte Backend-Anbindung verwendet wird.
 */
@Composable
fun HomePreviewRoute(
    initialCategoryId: String? = null,
    modifier: Modifier = Modifier,
    onMovieSelected: (String) -> Unit = {},
    onAddClicked: () -> Unit = {}
) {
    val stateHolder = remember { HomePreviewStateHolder(initialCategoryId = initialCategoryId) }

    LaunchedEffect(initialCategoryId) {
        if (stateHolder.state.selectedCategoryId != initialCategoryId) {
            stateHolder.onAction(HomeUiAction.CategorySelected(initialCategoryId))
        }
    }

    HomeScreen(
        state = stateHolder.state,
        onAction = { action ->
            stateHolder.onAction(action)
            if (action is HomeUiAction.MovieClicked) {
                onMovieSelected(action.movieId)
            }
            if (action == HomeUiAction.AddClicked) {
                onAddClicked()
            }
        },
        modifier = modifier
    )
}
