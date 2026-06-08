package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.components.SelectionOptionRow
import de.fh.erfurt.moviemate.presentation.movies.components.MovieCategoryTypes
import de.fh.erfurt.moviemate.presentation.movies.components.displayName
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Einfacher Filter-Dialog für den Homescreen.
 *
 * Der Dialog bündelt Kategorie und Sortierung hinter dem Filter-Icon, damit die
 * Topbar dem Mockup folgt.
 */
@Composable
fun HomeFilterDialog(
    selectedCategory: CategoryType?,
    selectedSortBy: SortBy,
    onCategorySelected: (CategoryType?) -> Unit,
    onSortSelected: (SortBy) -> Unit,
    onClearClicked: () -> Unit,
    onClearSortClicked: () -> Unit,
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
                        text = "Category",
                        style = MaterialTheme.typography.titleSmall,
                        color = MovieMateColors.PrimaryText,
                        modifier = Modifier.padding(bottom = MovieMateSpacing.dialogSectionBottomPadding)
                    )
                }

                item {
                    SelectionOptionRow(
                        label = "All categories",
                        selected = selectedCategory == null,
                        onClick = { onCategorySelected(null) }
                    )
                }

                items(MovieCategoryTypes) { category ->
                    SelectionOptionRow(
                        label = category.displayName(),
                        selected = selectedCategory == category,
                        onClick = { onCategorySelected(category) }
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
            TextButton(
                onClick = {
                    onClearClicked()
                    onClearSortClicked()
                }
            ) {
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
