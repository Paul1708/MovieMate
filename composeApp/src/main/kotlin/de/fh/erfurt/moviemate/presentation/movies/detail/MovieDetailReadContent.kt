package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import de.fh.erfurt.moviemate.movies.presentation.detail.MovieDetailUiModel
import de.fh.erfurt.moviemate.presentation.components.ScreenHeaderDefaults
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Lesende Detailansicht mit Poster, Metadaten, Bewertungen und Zusatzinfos.
 */
@Composable
internal fun MovieDetailReadContent(
    movie: MovieDetailUiModel,
    onAction: (MovieDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                start = ScreenHeaderDefaults.HorizontalPadding,
                top = ScreenHeaderDefaults.ContentTopSpacing,
                end = ScreenHeaderDefaults.HorizontalPadding,
                bottom = ScreenHeaderDefaults.VerticalPadding
            )
    ) {
        MovieDetailPoster(posterUrl = movie.posterUrl)

        Spacer(modifier = Modifier.height(MovieMateSpacing.detailMediumSpacing))

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

        Spacer(modifier = Modifier.height(MovieMateSpacing.detailContentSpacing))

        WatchedControl(
            isWatched = movie.isWatched,
            onWatchedChanged = {
                onAction(MovieDetailAction.WatchedChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(MovieMateSpacing.detailLargeSpacing))

        MovieDetailSection(
            title = "About Movie",
            body = movie.description.ifBlank { "No description available." }
        )

        Spacer(modifier = Modifier.height(MovieMateSpacing.sectionSpacing))

        MovieDetailSection(
            title = "Comment",
            body = movie.comment?.ifBlank { null } ?: "No comment available."
        )

        Spacer(modifier = Modifier.height(MovieMateSpacing.sectionSpacing))

        WatchProviderReadSection(
            providers = movie.watchProviders
        )

        Spacer(modifier = Modifier.height(MovieMateSpacing.sectionSpacing))

        DetailReadOnlyInfo(
            label = "${MovieMateIcons.updated} Last edited",
            value = movie.updatedAtLabel.ifBlank { "Unknown" }
        )
    }
}

/**
 * Zeigt Benutzerbewertung und IMDb-Bewertung getrennt nebeneinander.
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
            modifier = Modifier.weight(1f)
        )
        RatingCard(
            label = "IMDb",
            value = movie.imdbRating?.let { "$it/10" } ?: "None",
            visual = movie.imdbRating?.starText() ?: emptyStarText(),
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Kompakte Bewertungskarte mit Label, Sternanzeige und numerischem Wert.
 */
@Composable
private fun RatingCard(
    label: String,
    value: String,
    visual: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        color = MovieMateColors.CardBackground
    ) {
        Column(
            modifier = Modifier.padding(MovieMateSpacing.detailCardPadding)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MovieMateColors.SecondaryText
            )
            Text(
                text = visual,
                style = MaterialTheme.typography.titleSmall,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = MovieMateSpacing.detailTinySpacing)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MovieMateColors.PrimaryText,
                modifier = Modifier.padding(top = MovieMateSpacing.detailMicroSpacing)
            )
        }
    }
}

/**
 * Lesende Liste der verfügbaren Anbieter für den Film.
 */
@Composable
private fun WatchProviderReadSection(
    providers: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Watch Providers",
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
            Column(
                modifier = Modifier.padding(top = MovieMateSpacing.compactSpacing),
                verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.compactSpacing)
            ) {
                providers.forEach { provider ->
                    DetailReadOnlyInfo(
                        label = MovieMateIcons.provider,
                        value = provider
                    )
                }
            }
        }
    }
}
