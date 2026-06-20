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
import de.fh.erfurt.moviemate.presentation.movies.model.MovieUiModel
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Einzelne Filmkarte in der Homescreen-Liste.
 *
 * Die Karte zeigt links das Poster und rechts die wichtigsten Filmdaten:
 * Titel, Bewertung, Kategorie, Jahr und Laufzeit. Der Klick auf die Karte
 * ist bereits vorbereitet, damit später der Detail-Screen geöffnet werden kann.
 */
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
                    "Movie ${movie.title}, rating ${movie.ratingText}, category ${movie.categoryText}, year ${movie.yearText}"
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
            PosterPlaceholder(title = movie.title)

            Spacer(modifier = Modifier.width(MovieMateSpacing.movieCardPosterSpacing))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MovieMateColors.PrimaryText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(MovieMateSpacing.movieCardTextSpacing))

                MovieRatingRow(text = movie.ratingText)
                Spacer(modifier = Modifier.height(MovieMateSpacing.movieCardRatingMetaSpacing))
                MovieMetaRow(icon = MovieMateIcons.category, text = movie.categoryYearText())
                Spacer(modifier = Modifier.height(MovieMateSpacing.movieCardMetaSpacing))
                MovieMetaRow(icon = MovieMateIcons.runtime, text = movie.runtimeText)
            }

            Text(
                text = MovieMateIcons.more,
                style = MaterialTheme.typography.titleMedium,
                color = MovieMateColors.SecondaryText,
                modifier = Modifier.padding(start = MovieMateSpacing.movieCardTrailingIconStartPadding)
            )
        }
    }
}

/**
 * Platzhalter für das Filmposter.
 *
 * Diese Box steht an der Stelle, an der später das echte Poster aus `posterUri`
 * angezeigt wird. Im aktuellen UI-Schritt hilft sie, die Proportionen aus dem
 * Mockup nachzubauen.
 */
@Composable
private fun PosterPlaceholder(title: String) {
    val initial = title.firstOrNull()?.uppercaseChar()?.toString() ?: MovieMateIcons.image
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
        Text(
            text = initial,
            color = MovieMateColors.AccentLight,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MovieRatingRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.movieCardMetadataIconSpacing)
    ) {
        Text(
            text = MovieMateIcons.rating,
            style = MaterialTheme.typography.bodyMedium,
            color = MovieMateColors.Rating
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

/**
 * Kleine Informationszeile innerhalb einer Filmkarte.
 *
 * Sie kombiniert ein einfaches Symbol mit einem Text, zum Beispiel Stern plus
 * Bewertung oder Kalender plus Jahr. Dadurch bleiben die Metadaten der Karte
 * einheitlich ausgerichtet.
 */
@Composable
private fun MovieMetaRow(
    icon: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.movieCardMetadataIconSpacing)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.MutedText
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MovieMateColors.SecondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun MovieUiModel.categoryYearText(): String {
    return listOf(categoryText, yearText)
        .filter { it.isNotBlank() }
        .joinToString(separator = " - ")
}
