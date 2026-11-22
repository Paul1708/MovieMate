package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryGenreIcon
import de.fh.erfurt.moviemate.presentation.movies.components.MovieDateIcon
import de.fh.erfurt.moviemate.presentation.movies.components.MovieRuntimeIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

data class MovieDetailMetaItem(
    val icon: MovieDetailMetaIcon?,
    val label: String,
    val value: String
)

sealed class MovieDetailMetaIcon {
    data class Category(val iconKey: String?) : MovieDetailMetaIcon()
    object Runtime : MovieDetailMetaIcon()
    object Date : MovieDetailMetaIcon()
}

/**
 * Horizontale Übersicht der wichtigsten Filmdaten direkt unter dem Titel.
 */
@Composable
fun MovieDetailMetaRow(
    items: List<MovieDetailMetaItem>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
        contentPadding = PaddingValues()
    ) {
        items(items) { item ->
            MovieDetailMetaChip(item = item)
        }
    }
}

/**
 * Einzelner Metadaten-Chip mit Symbol und Wert.
 */
@Composable
private fun MovieDetailMetaChip(
    item: MovieDetailMetaItem,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.semantics {
            contentDescription = "${item.label}: ${item.value}"
        },
        shape = RoundedCornerShape(MovieMateShapes.chipRadius),
        color = MovieMateColors.SoftControl,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = MovieMateColors.SoftControlBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.detailChipHorizontalPadding,
                vertical = MovieMateSpacing.detailChipVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.detailMicroSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.icon?.let { MovieDetailMetaIconContent(icon = it) }
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodySmall,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MovieDetailMetaIconContent(
    icon: MovieDetailMetaIcon,
    modifier: Modifier = Modifier,
) {
    val iconModifier = modifier.size(MovieMateSpacing.detailChipIconSize)

    when (icon) {
        is MovieDetailMetaIcon.Category -> CategoryGenreIcon(
            iconKey = icon.iconKey,
            modifier = iconModifier
        )

        MovieDetailMetaIcon.Runtime -> MovieRuntimeIcon(
            modifier = iconModifier
        )

        MovieDetailMetaIcon.Date -> MovieDateIcon(
            modifier = iconModifier
        )
    }
}
