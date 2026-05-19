package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.presentation.movies.home.HomeScreen
import de.fh.erfurt.moviemate.presentation.movies.home.HomeUiAction

@Composable
fun HomePreviewRoute(
    initialCategory: CategoryType? = null,
    modifier: Modifier = Modifier,
    onMovieSelected: (String) -> Unit = {}
) {
    val stateHolder = remember { HomePreviewStateHolder(initialCategory = initialCategory) }

    LaunchedEffect(initialCategory) {
        if (stateHolder.state.selectedCategory != initialCategory) {
            stateHolder.onAction(HomeUiAction.CategorySelected(initialCategory))
        }
    }

    HomeScreen(
        state = stateHolder.state,
        onAction = { action ->
            stateHolder.onAction(action)
            if (action is HomeUiAction.MovieClicked) {
                onMovieSelected(action.movieId)
            }
        },
        modifier = modifier
    )
}
