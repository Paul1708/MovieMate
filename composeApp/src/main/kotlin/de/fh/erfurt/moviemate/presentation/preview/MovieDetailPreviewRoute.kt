package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailScreen

/**
 * Preview-Route für die Detailansicht mit lokalem Bearbeitungszustand.
 */
@Composable
fun MovieDetailPreviewRoute(
    movieId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stateHolder = remember(movieId) {
        MovieDetailPreviewStateHolder(movieId = movieId)
    }

    LaunchedEffect(movieId) {
        stateHolder.loadMovie(movieId)
    }

    MovieDetailScreen(
        state = stateHolder.state,
        mode = stateHolder.mode,
        editState = stateHolder.editState,
        availableCategories = stateHolder.availableCategories,
        onAction = stateHolder::onAction,
        onBackClicked = onBackClicked,
        onRetryClicked = {
            stateHolder.loadMovie(movieId)
        },
        modifier = modifier
    )
}
