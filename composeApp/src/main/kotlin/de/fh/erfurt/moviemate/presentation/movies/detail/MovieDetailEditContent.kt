package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.components.ScreenHeaderDefaults
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Bearbeitungsformular für alle änderbaren Detaildaten eines Films.
 */
@Composable
internal fun MovieDetailEditContent(
    movie: MovieDetailUiModel,
    editState: MovieDetailEditState,
    onAction: (MovieDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                start = ScreenHeaderDefaults.HorizontalPadding,
                top = ScreenHeaderDefaults.ContentTopSpacing,
                end = ScreenHeaderDefaults.HorizontalPadding,
                bottom = ScreenHeaderDefaults.VerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.titleSpacing)
    ) {
        MovieDetailPoster(
            posterUrl = movie.posterUrl,
            onClick = { onAction(MovieDetailAction.ImageClicked) },
            modifier = Modifier.height(MovieMateSpacing.detailEditPosterHeight)
        )

        DetailTextField(
            value = editState.title,
            label = "Title",
            onValueChange = { onAction(MovieDetailAction.TitleChanged(it)) }
        )

        CategorySelector(
            selectedCategoryId = editState.categoryId,
            onCategorySelected = { onAction(MovieDetailAction.CategoryChanged(it)) },
            onCustomCategoryChanged = { onAction(MovieDetailAction.CategoryChanged(it)) }
        )

        DetailTextField(
            value = editState.description,
            label = "About the movie",
            onValueChange = { onAction(MovieDetailAction.DescriptionChanged(it)) },
            minLines = 4
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
                value = editState.runtimeMinutes,
                label = "Runtime",
                onValueChange = { onAction(MovieDetailAction.RuntimeChanged(it)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        WatchedControl(
            isWatched = editState.isWatched,
            onWatchedChanged = {
                onAction(MovieDetailAction.WatchedChanged(it))
            }
        )

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
            label = "${MovieMateIcons.updated} Last edited",
            value = movie.updatedAtLabel.ifBlank { "Unknown" }
        )

        Button(
            onClick = { onAction(MovieDetailAction.SaveEditClicked) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save changes")
        }

        TextButton(
            onClick = { onAction(MovieDetailAction.CancelEditClicked) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}
