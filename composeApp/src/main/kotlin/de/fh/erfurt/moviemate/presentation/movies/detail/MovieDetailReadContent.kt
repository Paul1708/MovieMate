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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

@Composable
internal fun MovieDetailReadContent(
    movie: MovieDetailUiModel,
    onAction: (MovieDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
            .padding(
                top = MovieMateSpacing.compactSpacing,
                bottom = MovieMateSpacing.detailLargeSpacing,
            ),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.detailLargeSpacing),
    ) {
        MovieDetailPosterStage(posterUrl = movie.posterUrl, title = movie.title)

        Column(verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.titleSpacing)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { heading() },
            )
            val metadata = movie.metaItems()
            if (metadata.isNotEmpty()) {
                MovieDetailMetaRow(items = metadata)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.fieldSpacing)) {
            if (movie.userRating != null || movie.imdbRating != null) {
                RatingSummary(movie = movie)
            }
            WatchedStatusChip(
                isWatched = movie.isWatched,
                onWatchedChanged = { onAction(MovieDetailAction.WatchedChanged(it)) }
            )
        }

        if (movie.description.isNotBlank()) {
            MovieDetailSection(title = "Description", body = movie.description)
        }
        movie.comment?.takeIf { it.isNotBlank() }?.let {
            MovieDetailSection(title = "Comment", body = it)
        }
        if (movie.watchProviders.isNotEmpty()) {
            WatchProviderReadSection(providers = movie.watchProviders)
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
            value = movie.userRating?.let { "$it/10" },
            modifier = Modifier.weight(1f)
        )
        RatingCard(
            label = "IMDb rating",
            value = movie.imdbRating?.let { "$it/10" },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RatingCard(
    label: String,
    value: String?,
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
            modifier = Modifier.padding(MovieMateSpacing.detailCardPadding),
            verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.detailTinySpacing),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MovieMateColors.SecondaryText,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value != null) {
                    MovieMateIcon(
                        icon = MovieMateIcons.rating,
                        tint = MovieMateColors.Rating,
                        modifier = Modifier.size(MovieMateSpacing.detailChipIconSize),
                    )
                }
                Text(
                    text = value ?: "Not rated",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (value == null) MovieMateColors.SecondaryText else MovieMateColors.PrimaryText,
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
