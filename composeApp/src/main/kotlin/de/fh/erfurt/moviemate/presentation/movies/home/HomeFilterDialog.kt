package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.components.SelectionOptionRow
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryGenreIcon
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Einfacher Filter-Dialog für den Homescreen.
 */
@Composable
fun HomeFilterDialog(
    categories: List<CategoryUiModel>,
    selectedCategoryId: String?,
    selectedMediaType: MediaType?,
    onMediaTypeSelected: (MediaType?) -> Unit,
    selectedSortBy: SortBy,
    onCategorySelected: (String?) -> Unit,
    onSortSelected: (SortBy) -> Unit,
    onResetClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MovieMateColors.CardBackground,
        titleContentColor = MovieMateColors.PrimaryText,
        textContentColor = MovieMateColors.SecondaryText,
        title = {
            Text("Filter")
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = MovieMateSpacing.dialogMaxHeight)
            ) {
                item {
                    Text(
                        text = "Type",
                        style = MaterialTheme.typography.titleSmall,
                        color = MovieMateColors.PrimaryText,
                        modifier = Modifier.padding(bottom = MovieMateSpacing.dialogSectionBottomPadding),
                    )
                }

                items(listOf(null, MediaType.MOVIE, MediaType.SERIES)) { type ->
                    SelectionOptionRow(
                        label = when (type) {
                            null -> "All titles"
                            MediaType.MOVIE -> "Movies"
                            MediaType.SERIES -> "Series"
                        },
                        selected = selectedMediaType == type,
                        onClick = { onMediaTypeSelected(type) },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(MovieMateSpacing.titleSpacing))
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleSmall,
                        color = MovieMateColors.PrimaryText,
                        modifier = Modifier.padding(bottom = MovieMateSpacing.dialogSectionBottomPadding)
                    )
                }

                item {
                    SelectionOptionRow(
                        label = "All categories",
                        selected = selectedCategoryId == null,
                        onClick = { onCategorySelected(null) }
                    )
                }

                items(categories, key = { it.id }) { category ->
                    SelectionOptionRow(
                        label = category.name,
                        selected = selectedCategoryId == category.id,
                        onClick = { onCategorySelected(category.id) },
                        leadingContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryGenreIcon(
                                    iconKey = category.iconKey,
                                    modifier = Modifier.size(MovieMateSpacing.categoryIconSize)
                                )
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(MovieMateSpacing.titleSpacing))
                    Text(
                        text = "Sort",
                        style = MaterialTheme.typography.titleSmall,
                        color = MovieMateColors.PrimaryText,
                        modifier = Modifier.padding(bottom = MovieMateSpacing.dialogSectionBottomPadding)
                    )
                }

                items(SortBy.entries) { sortCriterion ->
                    SelectionOptionRow(
                        label = sortCriterion.displayName(),
                        selected = selectedSortBy == sortCriterion,
                        onClick = { onSortSelected(sortCriterion) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onResetClicked) {
                Text("Reset")
            }
        }
    )
}

/**
 * Converts the internal sort criterion into the label shown in the filter dialog.
 */
fun SortBy.displayName(): String {
    return when (this) {
        SortBy.TITLE -> "Title"
        SortBy.RELEASE_YEAR -> "Newest year"
        SortBy.UPDATED_AT -> "Recently updated"
        SortBy.RATING -> "Best rating"
        SortBy.RUNTIME -> "Runtime"
    }
}
