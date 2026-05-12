package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.domain.model.SortCriterion

/**
 * Eigener Dialog für die Sortierung der Homescreen-Liste.
 *
 * Die Sortierung ist bewusst vom Kategorie-Filter getrennt, damit beide
 * Aktionen in der Topbar eigene Buttons und eigene Zustände haben.
 */
@Composable
fun HomeSortDialog(
    selectedSortCriterion: SortCriterion,
    onSortSelected: (SortCriterion) -> Unit,
    onClearClicked: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sortierung")
        },
        text = {
            LazyColumn {
                items(SortCriterion.entries) { sortCriterion ->
                    SortOptionRow(
                        label = sortCriterion.displayName(),
                        selected = selectedSortCriterion == sortCriterion,
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
                Text("Clear")
            }
        }
    )
}

/**
 * Eine auswählbare Zeile für ein Sortierkriterium.
 */
@Composable
private fun SortOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
 * Wandelt das interne Sortierkriterium in einen gut lesbaren UI-Text um.
 */
fun SortCriterion.displayName(): String {
    return when (this) {
        SortCriterion.TITLE_ASC -> "Title A-Z"
        SortCriterion.TITLE_DESC -> "Title Z-A"
        SortCriterion.PUBLISHING_YEAR_DESC -> "Newest year"
        SortCriterion.RATING_DESC -> "Best rating"
        SortCriterion.RECENTLY_ADDED -> "Recently added"
    }
}
