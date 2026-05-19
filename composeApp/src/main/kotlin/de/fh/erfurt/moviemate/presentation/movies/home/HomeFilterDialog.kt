package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.presentation.components.SelectionOptionRow
import de.fh.erfurt.moviemate.presentation.movies.components.MovieCategoryTypes
import de.fh.erfurt.moviemate.presentation.movies.components.displayName

/**
 * Einfacher Filter-Dialog für den Homescreen.
 *
 * Dieser Dialog ist nur für Kategorien zuständig. Die Sortierung hat einen
 * eigenen Dialog, damit beide Bedienungen in der Topbar getrennt bleiben.
 */
@Composable
fun HomeFilterDialog(
    selectedCategory: CategoryType?,
    onCategorySelected: (CategoryType?) -> Unit,
    onClearClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Filter")
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 420.dp)
            ) {
                item {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
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

            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onClearClicked) {
                Text("Clear")
            }
        }
    )
}
