package de.fh.erfurt.moviemate.presentation.movies.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

@Composable
internal fun MediaTypeSelector(
    selected: MediaType,
    onSelected: (MediaType) -> Unit,
) {
    Row(
        modifier = Modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing),
    ) {
        for (type in MediaType.entries) {
            FilterChip(
                selected = type == selected,
                onClick = { onSelected(type) },
                label = { Text(if (type == MediaType.SERIES) "Series" else "Movie") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MovieMateColors.Accent,
                    selectedLabelColor = MovieMateColors.PrimaryText,
                    labelColor = MovieMateColors.PrimaryText,
                ),
            )
        }
    }
}

internal fun String.parseOptionalSeasonCount(): Int? {
    if (isBlank()) return null
    return trim().toIntOrNull()?.takeIf { it > 0 }
        ?: throw IllegalArgumentException("Seasons must be a positive whole number.")
}

internal fun seasonCountLabel(count: Int): String = if (count == 1) "1 season" else "$count seasons"
