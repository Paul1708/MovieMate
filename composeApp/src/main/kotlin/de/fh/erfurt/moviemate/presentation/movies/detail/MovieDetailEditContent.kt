package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.movies.components.MediaTypeSelector
import de.fh.erfurt.moviemate.presentation.movies.components.MovieLookupState
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Edit form for all changeable movie detail fields.
 */
@Composable
internal fun MovieDetailEditContent(
    movie: MovieDetailUiModel,
    editState: MovieDetailEditState,
    availableCategories: List<CategoryUiModel>,
    onAction: (MovieDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    lookup: MovieLookupState,
) {
    val isSeries = editState.mediaType == MediaType.SERIES
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = MovieMateSpacing.addFormBottomPadding),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.titleSpacing)
    ) {
        EditablePosterPicker(
            posterUrl = editState.posterUrl,
            onClick = { onAction(MovieDetailAction.ImageClicked) }
        )

        MediaTypeSelector(
            selected = editState.mediaType,
            onSelected = { onAction(MovieDetailAction.MediaTypeChanged(it)) },
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailTextField(
                value = editState.title,
                label = "Title",
                onValueChange = { onAction(MovieDetailAction.TitleChanged(it)) },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = { onAction(MovieDetailAction.FetchMovieDataClicked) },
                enabled = editState.title.isNotBlank() && !lookup.isLoading && !isSaving,
            ) {
                Text(if (lookup.isLoading) "Loading…" else "Fetch")
            }
        }
        lookup.error?.let { message ->
            Text(message, color = MovieMateColors.Rating, style = MaterialTheme.typography.bodySmall)
        }

        CategorySelector(
            selectedCategoryId = editState.categoryId,
            categories = availableCategories,
            onCategorySelected = { onAction(MovieDetailAction.CategoryChanged(it)) }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing)) {
            DetailTextField(
                value = editState.releaseYear,
                label = "Year",
                onValueChange = { onAction(MovieDetailAction.ReleaseYearChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            DetailTextField(
                value = if (isSeries) editState.totalSeasons else editState.runtimeMinutes,
                label = if (isSeries) "Seasons" else "Runtime",
                onValueChange = {
                    val action = if (isSeries) MovieDetailAction.TotalSeasonsChanged(it) else MovieDetailAction.RuntimeChanged(it)
                    onAction(action)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing)) {
            DetailTextField(
                value = editState.userRating,
                label = "User rating",
                onValueChange = { onAction(MovieDetailAction.UserRatingChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            DetailTextField(
                value = editState.imdbRating,
                label = "IMDb rating",
                onValueChange = { onAction(MovieDetailAction.ImdbRatingChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )
        }

        WatchedControl(
            isWatched = editState.isWatched,
            onWatchedChanged = {
                onAction(MovieDetailAction.WatchedChanged(it))
            }
        )

        DetailTextField(
            value = editState.description,
            label = "Description",
            onValueChange = { onAction(MovieDetailAction.DescriptionChanged(it)) },
            minLines = 4
        )

        DetailTextField(
            value = editState.comment,
            label = "Comment",
            onValueChange = { onAction(MovieDetailAction.CommentChanged(it)) },
            minLines = 3
        )

        WatchProvidersEditor(
            providers = editState.watchProviders,
            input = editState.watchProviderInput,
            onInputChanged = { onAction(MovieDetailAction.WatchProviderInputChanged(it)) },
            onAddClicked = { onAction(MovieDetailAction.WatchProviderAdded) },
            onRemoveClicked = { onAction(MovieDetailAction.WatchProviderRemoved(it)) }
        )

        DetailReadOnlyInfo(
            label = "Last edited",
            value = movie.updatedAtLabel.ifBlank { "Unknown" }
        )

        Button(
            onClick = { onAction(MovieDetailAction.SaveEditClicked) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving && !lookup.isLoading && lookup.results == null,
        ) {
            Text(if (isSaving) "Saving..." else "Save changes")
        }

        TextButton(
            onClick = { onAction(MovieDetailAction.CancelEditClicked) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Composable
private fun EditablePosterPicker(
    posterUrl: String?,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.CardBackground,
    ) {
        Row(
            modifier = Modifier.padding(MovieMateSpacing.detailCardPadding),
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.detailMediumSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MovieDetailPoster(
                posterUrl = posterUrl,
                modifier = Modifier.size(width = 96.dp, height = MovieMateSpacing.addPosterHeight),
                showPlaceholderIcon = true,
                contentScale = ContentScale.Fit,
            )
            Column(verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.detailMicroSpacing)) {
                Text(
                    text = if (posterUrl.isNullOrBlank()) "Add poster" else "Change poster",
                    style = MaterialTheme.typography.titleSmall,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Choose an image from your device",
                    style = MaterialTheme.typography.bodySmall,
                    color = MovieMateColors.SecondaryText,
                )
            }
        }
    }
}
