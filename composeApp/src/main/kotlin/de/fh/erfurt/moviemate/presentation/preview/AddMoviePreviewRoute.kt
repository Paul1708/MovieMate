package de.fh.erfurt.moviemate.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.movies.add.AddMovieAction
import de.fh.erfurt.moviemate.presentation.movies.add.AddMovieScreen

/**
 * Preview route for the add movie UI until the backend-backed flow is wired.
 */
@Composable
fun AddMoviePreviewRoute(
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit = {}
) {
    val stateHolder = remember { AddMoviePreviewStateHolder() }

    AddMovieScreen(
        state = stateHolder.state,
        onAction = { action ->
            stateHolder.onAction(action)

            if (action == AddMovieAction.CancelClicked) {
                onCloseClicked()
            }
        },
        modifier = modifier
    )
}
