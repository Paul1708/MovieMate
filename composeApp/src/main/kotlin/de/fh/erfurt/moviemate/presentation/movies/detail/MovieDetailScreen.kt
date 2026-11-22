package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiState
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.components.ScreenEmptyState
import de.fh.erfurt.moviemate.presentation.components.ScreenErrorState
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.components.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.movies.components.MovieFormTopBar
import de.fh.erfurt.moviemate.presentation.movies.components.MovieLookupState
import de.fh.erfurt.moviemate.presentation.movies.components.MovieMatchDialog
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.preview.previewCategories
import de.fh.erfurt.moviemate.presentation.preview.previewMovieDetailUiState
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Oberster Detail-Screen für Anzeige, Bearbeitung und Ladezustände eines Films.
 */
@Composable
fun MovieDetailScreen(
    state: MovieDetailUiState,
    mode: MovieDetailMode,
    editState: MovieDetailEditState,
    availableCategories: List<CategoryUiModel>,
    onAction: (MovieDetailAction) -> Unit,
    onBackClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    lookup: MovieLookupState = MovieLookupState(),
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

    MovieMateScreenBackground(modifier = modifier) {
        when {
            errorMessage != null -> MovieDetailStateScaffold(
                onBackClicked = onBackClicked
            ) {
                ScreenErrorState(
                    message = errorMessage,
                    onRetryClicked = onRetryClicked,
                    modifier = Modifier.padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                )
            }

            state.isLoading -> MovieDetailStateScaffold(
                onBackClicked = onBackClicked
            ) {
                ScreenLoadingState(
                    message = "Loading title...",
                    modifier = Modifier.padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                )
            }

            movie == null -> MovieDetailStateScaffold(
                onBackClicked = onBackClicked
            ) {
                ScreenEmptyState(
                    message = "Title not found",
                    modifier = Modifier.padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                )
            }

            isEditMode -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                        .padding(top = MovieMateScreenDefaults.VerticalPadding)
                ) {
                    MovieFormTopBar(
                        title = "Edit title",
                        navigationIcon = MovieMateIcons.back,
                        navigationContentDescription = "Go back",
                        onNavigationClicked = { leaveEditMode() }
                    )

                    Spacer(modifier = Modifier.height(MovieMateScreenDefaults.ContentTopSpacing))

                    MovieDetailEditContent(
                        movie = movie,
                        editState = editState,
                        availableCategories = availableCategories,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize(),
                        isSaving = isSaving,
                        lookup = lookup,
                    )
                }
            }

            else -> Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MovieDetailTopBar(
                    onBackClicked = onBackClicked,
                    onEditClicked = { onAction(MovieDetailAction.EditClicked) },
                    modifier = Modifier
                        .widthIn(max = MovieMateSpacing.detailMaxWidth)
                        .padding(top = MovieMateScreenDefaults.VerticalPadding),
                )
                MovieDetailReadContent(
                    movie = movie,
                    onAction = onAction,
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = MovieMateSpacing.detailMaxWidth)
                        .fillMaxWidth(),
                )
            }
        }
    }

    if (isEditMode) {
        lookup.results?.let { results ->
            MovieMatchDialog(
                results = results,
                onSelect = { onAction(MovieDetailAction.MovieMatchSelected(it)) },
                onDismiss = { onAction(MovieDetailAction.MovieMatchesDismissed) },
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

@Composable
private fun MovieDetailStateScaffold(
    onBackClicked: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MovieDetailTopBar(
            title = "Title details",
            onBackClicked = onBackClicked,
            modifier = Modifier.padding(top = MovieMateScreenDefaults.VerticalPadding)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = MovieMateScreenDefaults.ContentTopSpacing),
            content = content
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
        availableCategories = previewCategories(),
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
        availableCategories = previewCategories(),
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
        availableCategories = previewCategories(),
        onAction = {},
        onBackClicked = {},
        onRetryClicked = {}
    )
}
