package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import de.fh.erfurt.moviemate.movies.domain.repository.movie.SortBy
import de.fh.erfurt.moviemate.presentation.components.SelectionOptionRow
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/**
 * Eigener Dialog für die Sortierung der Homescreen-Liste.
 *
 * Die Sortierung ist bewusst vom Kategorie-Filter getrennt, damit beide
 * Aktionen in der Topbar eigene Buttons und eigene Zustände haben.
 */
@Composable
fun HomeSortDialog(
    selectedSortBy: SortBy,
    onSortSelected: (SortBy) -> Unit,
    onClearClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MovieMateColors.CardBackground,
        titleContentColor = MovieMateColors.PrimaryText,
        textContentColor = MovieMateColors.SecondaryText,
        title = {
            Text("Sort")
        },
        text = {
            LazyColumn {
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
            TextButton(onClick = onClearClicked) {
                Text("Reset")
            }
        }
    )
}

/**
 * Wandelt das interne Sortierkriterium in einen gut lesbaren UI-Text um.
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
