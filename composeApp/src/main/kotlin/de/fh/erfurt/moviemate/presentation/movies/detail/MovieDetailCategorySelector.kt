package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryGenreIcon
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryUiModel
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Category picker for existing custom categories.
 */
@Composable
internal fun CategorySelector(
    selectedCategoryId: String,
    categories: List<CategoryUiModel>,
    onCategorySelected: (String) -> Unit,
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

        if (categories.isEmpty()) {
            Text(
                text = "Create a category first in the Categories tab.",
                style = MaterialTheme.typography.bodySmall,
                color = MovieMateColors.SecondaryText
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryChoice(
                        category = category,
                        selected = selectedCategoryId == category.id,
                        onClick = { onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
}

/**
 * Selection chip for an existing category.
 */
@Composable
private fun CategoryChoice(
    category: CategoryUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = if (selected) MovieMateColors.SoftControl else MovieMateColors.CardBackground,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = if (selected) MovieMateColors.AccentLight else MovieMateColors.CardBorder
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.detailChipHorizontalPadding,
                vertical = MovieMateSpacing.compactSpacing
            ),
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryGenreIcon(
                iconKey = category.iconKey,
                modifier = Modifier.size(MovieMateSpacing.categoryIconSize)
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall,
                color = MovieMateColors.PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
