package de.fh.erfurt.moviemate.presentation.movies.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import de.fh.erfurt.moviemate.presentation.movies.categories.CategoryGenreIcon
import de.fh.erfurt.moviemate.presentation.movies.detail.MovieDetailPoster
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun MovieCard(
    movie: MovieUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(MovieMateSpacing.movieCardHeight)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .semantics {
                contentDescription =
                    "Movie ${movie.title}, ${if (movie.isWatched) "watched" else "watch later"}, " +
                    "rating ${movie.ratingText}, category ${movie.categoryText}, year ${movie.yearText}"
            },
        shape = RoundedCornerShape(MovieMateShapes.cardRadius),
        colors = CardDefaults.cardColors(containerColor = MovieMateColors.CardBackground),
        border = BorderStroke(MovieMateSpacing.dividerThickness, MovieMateColors.CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = MovieMateSpacing.noElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(MovieMateSpacing.cardPadding),
            verticalAlignment = Alignment.Top
        ) {
            MoviePoster(
                title = movie.title,
                posterUri = movie.posterUri,
            )

            Spacer(modifier = Modifier.width(MovieMateSpacing.movieCardPosterSpacing))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.movieCardTitleBadgeSpacing),
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MovieMateColors.PrimaryText,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )

                    if (movie.isWatched) {
                        WatchedBadge()
                    }
                }

                Spacer(modifier = Modifier.height(MovieMateSpacing.movieCardTextSpacing))

                MovieRatingRow(text = movie.ratingText)
                Spacer(modifier = Modifier.height(MovieMateSpacing.movieCardRatingMetaSpacing))
                MovieCategoryDateRow(movie = movie)
                Spacer(modifier = Modifier.height(MovieMateSpacing.movieCardMetaSpacing))
                MovieMetaRow(
                    icon = {
                        MovieRuntimeIcon(
                            modifier = Modifier.size(MovieMateSpacing.movieCardMetadataIconSize)
                        )
                    },
                    text = movie.runtimeText
                )
            }

        }
    }
}

@Composable
private fun MoviePoster(
    title: String,
    posterUri: String?,
) {
    if (!posterUri.isNullOrBlank()) {
        MovieDetailPoster(
            posterUrl = posterUri,
            modifier = Modifier.size(
                width = MovieMateSpacing.moviePosterWidth,
                height = MovieMateSpacing.moviePosterHeight
            )
        )
        return
    }

    val initial = title.firstOrNull()?.uppercaseChar()?.toString()
    Box(
        modifier = Modifier
            .size(width = MovieMateSpacing.moviePosterWidth, height = MovieMateSpacing.moviePosterHeight)
            .clip(RoundedCornerShape(MovieMateShapes.posterRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MovieMateColors.AccentContainer,
                        MovieMateColors.PosterPlaceholder,
                        MovieMateColors.CardBackgroundElevated
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (initial == null) {
            MovieMateIcon(
                icon = MovieMateIcons.image,
                tint = MovieMateColors.AccentLight,
                modifier = Modifier.size(MovieMateSpacing.posterPlaceholderIconSize)
            )
        } else {
            Text(
                text = initial,
                color = MovieMateColors.AccentLight,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MovieRatingRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.movieCardMetadataIconSpacing)
    ) {
        MovieMateIcon(
            icon = MovieMateIcons.rating,
            tint = MovieMateColors.Rating,
            modifier = Modifier.size(MovieMateSpacing.movieCardMetadataIconSize),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MovieMateColors.PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WatchedBadge() {
    Surface(
        shape = RoundedCornerShape(MovieMateShapes.chipRadius),
        color = MovieMateColors.AccentContainer,
        contentColor = MovieMateColors.AccentLight,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MovieMateSpacing.movieCardStatusHorizontalPadding,
                vertical = MovieMateSpacing.movieCardStatusVerticalPadding,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.detailMicroSpacing),
        ) {
            MovieMateIcon(
                icon = MovieMateIcons.watched,
                tint = MovieMateColors.AccentLight,
                modifier = Modifier.size(MovieMateSpacing.movieCardStatusIconSize),
            )
            Text(
                text = "Watched",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MovieMetaRow(
    icon: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.movieCardMetadataIconSpacing)
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MovieCategoryDateRow(movie: MovieUiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.movieCardMetadataGroupSpacing)
    ) {
        MovieMetaRow(
            icon = {
                CategoryGenreIcon(
                    iconKey = movie.categoryIconKey,
                    modifier = Modifier.size(MovieMateSpacing.movieCardMetadataIconSize)
                )
            },
            text = movie.categoryText,
            modifier = Modifier.weight(1f, fill = false)
        )

        if (movie.yearText.isNotBlank()) {
            MovieMetaRow(
                icon = {
                    MovieDateIcon(
                        modifier = Modifier.size(MovieMateSpacing.movieCardMetadataIconSize)
                    )
                },
                text = movie.yearText
            )
        }
    }
}
