package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiState
import de.fh.erfurt.moviemate.presentation.components.ScreenEmptyState
import de.fh.erfurt.moviemate.presentation.components.ScreenErrorState
import de.fh.erfurt.moviemate.presentation.components.ScreenHeaderDefaults
import de.fh.erfurt.moviemate.presentation.components.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.preview.previewMovieDetailUiState
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/**
 * Oberster Detail-Screen für Anzeige, Bearbeitung und Ladezustände eines Films.
 */
@Composable
fun MovieDetailScreen(
    state: MovieDetailUiState,
    mode: MovieDetailMode,
    editState: MovieDetailEditState,
    onAction: (MovieDetailAction) -> Unit,
    onBackClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val errorMessage = state.errorMessage
    val movie = state.movie
    val isEditMode = mode == MovieDetailMode.Edit
    val hasUnsavedChanges = movie?.let { editState != MovieDetailEditState.from(it) } == true
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    fun leaveEditMode() {
        if (hasUnsavedChanges) {
            showDiscardDialog = true
        } else {
            onAction(MovieDetailAction.CancelEditClicked)
        }
    }

    BackHandler(enabled = isEditMode) {
        leaveEditMode()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MovieMateColors.ScreenBackground)
    ) {
        MovieDetailTopBar(
            title = "Detail",
            onBackClicked = {
                if (isEditMode) {
                    leaveEditMode()
                } else {
                    onBackClicked()
                }
            },
            actionLabel = movie?.let { if (isEditMode) "Cancel" else "Edit" },
            onActionClicked = {
                if (isEditMode) {
                    leaveEditMode()
                } else {
                    onAction(MovieDetailAction.EditClicked)
                }
            }
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = ScreenHeaderDefaults.DividerThickness,
            color = MovieMateColors.Divider
        )

        when {
            errorMessage != null -> ScreenErrorState(
                message = errorMessage,
                onRetryClicked = onRetryClicked,
                modifier = Modifier.padding(horizontal = ScreenHeaderDefaults.HorizontalPadding)
            )

            state.isLoading -> ScreenLoadingState(
                message = "Loading movie...",
                modifier = Modifier.padding(horizontal = ScreenHeaderDefaults.HorizontalPadding)
            )

            movie == null -> ScreenEmptyState(
                message = "Movie not found",
                modifier = Modifier.padding(horizontal = ScreenHeaderDefaults.HorizontalPadding)
            )

            isEditMode -> MovieDetailEditContent(
                movie = movie,
                editState = editState,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )

            else -> MovieDetailReadContent(
                movie = movie,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showDiscardDialog) {
        DiscardEditDialog(
            onDiscard = {
                showDiscardDialog = false
                onAction(MovieDetailAction.CancelEditClicked)
            },
            onKeepEditing = {
                showDiscardDialog = false
            }
        )
    }
}

/**
 * Sicherheitsdialog, wenn der Bearbeitungsmodus ungespeicherte Änderungen enthält.
 */
@Composable
private fun DiscardEditDialog(
    onDiscard: () -> Unit,
    onKeepEditing: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onKeepEditing,
        title = { Text("Discard changes?") },
        text = { Text("Your edits are not saved yet.") },
        confirmButton = {
            TextButton(onClick = onDiscard) {
                Text("Discard")
            }
        },
        dismissButton = {
            TextButton(onClick = onKeepEditing) {
                Text("Keep editing")
            }
        }
    )
}

/**
 * Preview der normalen lesenden Detailansicht.
 */
@Preview
@Composable
private fun MovieDetailScreenPreview() {
    val state = previewMovieDetailUiState()

    MovieDetailScreen(
        state = state,
        mode = MovieDetailMode.View,
        editState = state.movie?.let(MovieDetailEditState::from) ?: MovieDetailEditState(),
        onAction = {},
        onBackClicked = {},
        onRetryClicked = {}
    )
}

/**
 * Preview der Detailansicht im Bearbeitungsmodus.
 */
@Preview
@Composable
private fun MovieDetailScreenEditPreview() {
    val state = previewMovieDetailUiState()

    MovieDetailScreen(
        state = state,
        mode = MovieDetailMode.Edit,
        editState = state.movie?.let(MovieDetailEditState::from) ?: MovieDetailEditState(),
        onAction = {},
        onBackClicked = {},
        onRetryClicked = {}
    )
}

/**
 * Preview für den Ladezustand der Detailansicht.
 */
@Preview
@Composable
private fun MovieDetailScreenLoadingPreview() {
    MovieDetailScreen(
        state = MovieDetailUiState(isLoading = true),
        mode = MovieDetailMode.View,
        editState = MovieDetailEditState(),
        onAction = {},
        onBackClicked = {},
        onRetryClicked = {}
    )
}
