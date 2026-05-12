package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.domain.model.CategoryType
import de.fh.erfurt.moviemate.domain.model.SortCriterion

/**
 * Einfacher Filter-Dialog für den Homescreen.
 *
 * Der Dialog ist noch ein UI-Platzhalter, funktioniert aber bereits mit den
 * Mockdaten. Später können dieselben Werte an `GetMoviesUseCase` übergeben
 * werden, damit Backend oder lokale Datenbank die Filterung übernehmen.
 */
@Composable
fun HomeFilterDialog(
    selectedCategory: CategoryType?,
    selectedSortCriterion: SortCriterion,
    onCategorySelected: (CategoryType?) -> Unit,
    onSortSelected: (SortCriterion) -> Unit,
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
                        text = "Kategorie",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                item {
                    FilterOptionRow(
                        label = "Alle Kategorien",
                        selected = selectedCategory == null,
                        onClick = { onCategorySelected(null) }
                    )
                }

                items(CategoryType.entries) { category ->
                    FilterOptionRow(
                        label = category.displayName(),
                        selected = selectedCategory == category,
                        onClick = { onCategorySelected(category) }
                    )
                }

                item {
                    Text(
                        text = "Sortierung",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }

                items(SortCriterion.entries) { sortCriterion ->
                    FilterOptionRow(
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
 * Eine auswählbare Zeile im Filter-Dialog.
 *
 * Sie wird sowohl für Kategorien als auch für Sortieroptionen genutzt, damit
 * der Dialog gleichmäßig aufgebaut bleibt.
 */
@Composable
private fun FilterOptionRow(
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
 * Wandelt den internen Kategorie-Namen in einen gut lesbaren UI-Text um.
 */
fun CategoryType.displayName(): String {
    return when (this) {
        CategoryType.DRAMA -> "Drama"
        CategoryType.ACTION -> "Action"
        CategoryType.FANTASY -> "Fantasy"
        CategoryType.ROMANCE -> "Romance"
        CategoryType.COMEDY -> "Comedy"
        CategoryType.HORROR -> "Horror"
        CategoryType.FAVORITE -> "Favorite"
        CategoryType.SCIENCE_FICTION -> "Science Fiction"
        CategoryType.ANIMATION -> "Animation"
        CategoryType.OTHER -> "Other"
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
