package de.fh.erfurt.moviemate.presentation.movies.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.components.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.movies.components.MovieFormTopBar
import de.fh.erfurt.moviemate.presentation.movies.detail.CategorySelector
import de.fh.erfurt.moviemate.presentation.movies.detail.DetailTextField
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailPoster
import de.fh.erfurt.moviemate.presentation.movies.detail.WatchProvidersEditor
import de.fh.erfurt.moviemate.presentation.movies.detail.WatchedControl
import de.fh.erfurt.moviemate.presentation.preview.sampleCategories
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Stateless add-movie form. Persistence is coordinated by AddMovieViewModel.
 */
@Composable
fun AddMovieScreen(
    state: AddMovieUiState,
    onAction: (AddMovieAction) -> Unit,
    modifier: Modifier = Modifier
) {
    MovieMateScreenBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                    .padding(top = MovieMateScreenDefaults.VerticalPadding)
            ) {
                MovieFormTopBar(
                    title = "Add movie",
                    navigationIcon = MovieMateIcons.remove,
                    navigationContentDescription = "Cancel add movie",
                    onNavigationClicked = { onAction(AddMovieAction.CancelClicked) },
                    actionLabel = "Save",
                    actionEnabled = state.canSave,
                    onActionClicked = { onAction(AddMovieAction.SaveClicked) }
                )

                Spacer(modifier = Modifier.height(MovieMateScreenDefaults.ContentTopSpacing))

                when {
                    state.isLoading -> ScreenLoadingState(message = "Loading categories...")
                    else -> {
                        AddMovieStatus(state = state)
                        AddMovieForm(
                            state = state,
                            onAction = onAction,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            if (!state.isLoading && !state.isFetching && !state.isSaving) {
                ExtendedFloatingActionButton(
                    onClick = { onAction(AddMovieAction.FetchMovieDataClicked) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = MovieMateScreenDefaults.HorizontalPadding,
                            bottom = MovieMateSpacing.detailFloatingButtonBottomPadding
                        )
                        .semantics { contentDescription = "Fetch movie data" },
                    containerColor = MovieMateColors.Accent,
                    contentColor = MovieMateColors.PrimaryText
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MovieMateIcon(
                            icon = MovieMateIcons.search,
                            tint = MovieMateColors.PrimaryText,
                            modifier = Modifier.size(MovieMateSpacing.inlineActionIconSize)
                        )
                        Text(
                            text = "Fetch",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMovieStatus(
    state: AddMovieUiState,
) {
    val message = when {
        state.isSaving -> "Saving movie..."
        state.isFetching -> "Fetching movie data..."
        state.errorMessage != null -> state.errorMessage
        else -> null
    } ?: return

    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = if (state.errorMessage != null) MovieMateColors.Rating else MovieMateColors.SecondaryText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = MovieMateSpacing.compactSpacing)
    )
}

@Composable
private fun AddMovieForm(
    state: AddMovieUiState,
    onAction: (AddMovieAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = MovieMateSpacing.addFormBottomPadding),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.titleSpacing)
    ) {
        AddPosterPicker(
            posterUrl = state.posterUrl,
            onClick = { onAction(AddMovieAction.PosterClicked) }
        )

        DetailTextField(
            value = state.title,
            label = "Title *",
            placeholder = "Movie name",
            onValueChange = { onAction(AddMovieAction.TitleChanged(it)) }
        )

        CategorySelector(
            selectedCategoryId = state.categoryId,
            categories = state.availableCategories,
            onCategorySelected = { onAction(AddMovieAction.CategoryChanged(it)) }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing)) {
            DetailTextField(
                value = state.releaseYear,
                label = "Year",
                placeholder = "2026",
                onValueChange = { onAction(AddMovieAction.ReleaseYearChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            DetailTextField(
                value = state.runtimeMinutes,
                label = "Runtime",
                placeholder = "120 min",
                onValueChange = { onAction(AddMovieAction.RuntimeChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing)) {
            DetailTextField(
                value = state.userRating,
                label = "User rating",
                placeholder = "1-10",
                onValueChange = { onAction(AddMovieAction.UserRatingChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            DetailTextField(
                value = state.imdbRating,
                label = "IMDb rating",
                placeholder = "0.0-10.0",
                onValueChange = { onAction(AddMovieAction.ImdbRatingChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        WatchedControl(
            isWatched = state.isWatched,
            onWatchedChanged = { onAction(AddMovieAction.WatchedChanged(it)) }
        )

        DetailTextField(
            value = state.description,
            label = "About the movie *",
            placeholder = "Add a short description",
            onValueChange = { onAction(AddMovieAction.DescriptionChanged(it)) },
            minLines = 4
        )

        DetailTextField(
            value = state.comment,
            label = "Comment",
            placeholder = "Add a comment",
            onValueChange = { onAction(AddMovieAction.CommentChanged(it)) },
            minLines = 3
        )

        WatchProvidersEditor(
            providers = state.watchProviders,
            input = state.watchProviderInput,
            onInputChanged = { onAction(AddMovieAction.WatchProviderInputChanged(it)) },
            onAddClicked = { onAction(AddMovieAction.WatchProviderAdded) },
            onRemoveClicked = { onAction(AddMovieAction.WatchProviderRemoved(it)) }
        )
    }
}

@Composable
private fun AddPosterPicker(
    posterUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MovieMateSpacing.addPosterHeight)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Add poster" }
    ) {
        MovieDetailPoster(
            posterUrl = posterUrl,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(MovieMateSpacing.compactSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MovieMateIcon(
                icon = MovieMateIcons.image,
                tint = MovieMateColors.AccentLight,
                secondaryTint = MovieMateColors.Accent,
                modifier = Modifier.size(MovieMateSpacing.posterActionIconSize)
            )
            Text(
                text = if (posterUrl.isNullOrBlank()) "Add poster" else "Change poster",
                style = MaterialTheme.typography.labelLarge,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = MovieMateSpacing.compactSpacing)
            )
            Text(
                text = "Tap to choose",
                style = MaterialTheme.typography.bodySmall,
                color = MovieMateColors.SecondaryText,
                modifier = Modifier.padding(top = MovieMateSpacing.detailMicroSpacing)
            )
        }
    }
}

@Preview
@Composable
private fun AddMovieScreenPreview() {
    AddMovieScreen(
        state = AddMovieUiState(availableCategories = sampleCategories()),
        onAction = {}
    )
}
