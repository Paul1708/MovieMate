package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.presentation.movies.components.MovieCategoryTypes
import de.fh.erfurt.moviemate.presentation.movies.components.displayName
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Kategorieauswahl mit bekannten Kategorien und freier Eingabe für neue Kategorien.
 */
@Composable
internal fun CategorySelector(
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    onCustomCategoryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing)
    ) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(MovieCategoryTypes) { category ->
                CategoryChoice(
                    label = category.displayName(),
                    selected = selectedCategoryId == category.storageKey,
                    onClick = { onCategorySelected(category.storageKey) }
                )
            }
        }

        DetailTextField(
            value = selectedCategoryId,
            label = "Custom category",
            onValueChange = onCustomCategoryChanged
        )
    }
}

/**
 * Auswahlchip für eine vorhandene Kategorie.
 */
@Composable
private fun CategoryChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = if (selected) MovieMateColors.SoftControl else MovieMateColors.CardBackground,
        onClick = onClick
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.PrimaryText,
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.detailChipHorizontalPadding,
                vertical = MovieMateSpacing.compactSpacing
            )
        )
    }
}
