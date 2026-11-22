package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Read-only movie detail view in the dark mockup style.
 */
@Composable
internal fun MovieDetailReadContent(
    movie: MovieDetailUiModel,
    onAction: (MovieDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = MovieMateSpacing.detailReadBottomPadding)
        ) {
            MovieDetailPoster(
                posterUrl = movie.posterUrl,
                modifier = Modifier.height(MovieMateSpacing.detailHeroHeight)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MovieMateScreenDefaults.HorizontalPadding,
                        vertical = MovieMateSpacing.detailMediumSpacing
                    )
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(MovieMateSpacing.titleSpacing))

                MovieDetailMetaRow(items = movie.metaItems())

                Spacer(modifier = Modifier.height(MovieMateSpacing.detailContentSpacing))

                RatingSummary(movie = movie)

                Spacer(modifier = Modifier.height(MovieMateSpacing.titleSpacing))

                WatchedStatusChip(
                    isWatched = movie.isWatched,
                    onWatchedChanged = { onAction(MovieDetailAction.WatchedChanged(it)) }
                )

                Spacer(modifier = Modifier.height(MovieMateSpacing.detailLargeSpacing))

                if (movie.description.isNotBlank()) {
                    MovieDetailSection(title = "Description", body = movie.description)
                    Spacer(modifier = Modifier.height(MovieMateSpacing.sectionSpacing))
                }

                MovieDetailSection(
                    title = "Comment",
                    body = movie.comment?.ifBlank { null } ?: "No comment available."
                )

                Spacer(modifier = Modifier.height(MovieMateSpacing.sectionSpacing))

                WatchProviderReadSection(providers = movie.watchProviders)
            }
        }

        FloatingActionButton(
            onClick = { onAction(MovieDetailAction.EditClicked) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = MovieMateScreenDefaults.HorizontalPadding,
                    bottom = MovieMateSpacing.detailFloatingButtonBottomPadding
                ),
            shape = CircleShape,
            containerColor = MovieMateColors.Accent,
            contentColor = MovieMateColors.PrimaryText
        ) {
            MovieMateIcon(
                icon = MovieMateIcons.edit,
                tint = MovieMateColors.PrimaryText,
                contentDescription = "Edit title",
                modifier = Modifier.size(MovieMateSpacing.fabIconSize),
            )
        }
    }
}

/**
 * Shows user rating and IMDb rating as separate cards.
 */
@Composable
private fun RatingSummary(
    movie: MovieDetailUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing)
    ) {
        RatingCard(
            label = "User rating",
            value = movie.userRating?.let { "$it/10" } ?: "None",
            visual = movie.userRating?.toDouble()?.starText() ?: emptyStarText(),
            visualColor = MovieMateColors.Rating,
            modifier = Modifier.weight(1f)
        )
        RatingCard(
            label = "IMDb rating",
            value = movie.imdbRating?.let { "$it/10" } ?: "None",
            visual = "IMDb",
            visualColor = MovieMateColors.Rating,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RatingCard(
    label: String,
    value: String,
    visual: String,
    visualColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.CardBackgroundElevated,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = MovieMateColors.CardBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(MovieMateSpacing.detailCardPadding)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MovieMateColors.SecondaryText,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(MovieMateSpacing.detailTinySpacing))

            Row(
                horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visual,
                    style = MaterialTheme.typography.labelMedium,
                    color = visualColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WatchProviderReadSection(
    providers: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Providers",
            style = MaterialTheme.typography.titleMedium,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.Bold
        )

        if (providers.isEmpty()) {
            Text(
                text = "No providers available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MovieMateColors.SecondaryText,
                modifier = Modifier.padding(top = MovieMateSpacing.compactSpacing)
            )
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MovieMateSpacing.compactSpacing),
                horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing)
            ) {
                items(providers, key = { it }) { provider ->
                    ProviderChip(provider = provider)
                }
            }
        }
    }
}

@Composable
private fun ProviderChip(
    provider: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MovieMateShapes.chipRadius),
        color = MovieMateColors.SoftControl,
        border = BorderStroke(
            width = MovieMateSpacing.dividerThickness,
            color = MovieMateColors.SoftControlBorder
        )
    ) {
        Text(
            text = provider,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.detailChipHorizontalPadding,
                vertical = MovieMateSpacing.detailChipVerticalPadding
            )
        )
    }
}
