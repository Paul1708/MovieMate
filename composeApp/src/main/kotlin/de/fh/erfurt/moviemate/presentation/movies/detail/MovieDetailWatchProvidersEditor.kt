package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Editor für Anbieter: neue Anbieter hinzufügen und bestehende Chips entfernen.
 */
@Composable
internal fun WatchProvidersEditor(
    providers: List<String>,
    input: String,
    onInputChanged: (String) -> Unit,
    onAddClicked: () -> Unit,
    onRemoveClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing)
    ) {
        Text(
            text = "Providers",
            style = MaterialTheme.typography.titleSmall,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.SemiBold
        )

        if (providers.isEmpty()) {
            Text(
                text = "No providers selected.",
                style = MaterialTheme.typography.bodySmall,
                color = MovieMateColors.SecondaryText
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(providers, key = { it }) { provider ->
                    WatchProviderChip(
                        provider = provider,
                        onRemoveClicked = { onRemoveClicked(provider) }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailTextField(
                value = input,
                label = "Add provider",
                onValueChange = onInputChanged,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onAddClicked,
                enabled = input.isNotBlank()
            ) {
                MovieMateIcon(
                    icon = MovieMateIcons.add,
                    tint = MovieMateColors.PrimaryText,
                    contentDescription = "Add provider",
                    modifier = Modifier.size(MovieMateSpacing.inlineActionIconSize),
                )
            }
        }
    }
}

/**
 * Einzelner Anbieterchip mit Entfernen-Aktion.
 */
@Composable
private fun WatchProviderChip(
    provider: String,
    onRemoveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.semantics {
            contentDescription = "Watch provider $provider"
        },
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.SoftControl
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.detailChipHorizontalPadding,
                vertical = MovieMateSpacing.detailChipVerticalPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = provider,
                style = MaterialTheme.typography.bodySmall,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.SemiBold
            )
            MovieMateIcon(
                icon = MovieMateIcons.close,
                tint = MovieMateColors.SecondaryText,
                modifier = Modifier
                    .size(MovieMateSpacing.detailChipIconSize)
                    .clickable(onClick = onRemoveClicked)
                    .semantics { contentDescription = "Remove $provider" }
            )
        }
    }
}
