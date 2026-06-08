package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Posterfläche der Detailansicht; im Edit-Modus als Auswahlfläche anklickbar.
 */
@Composable
fun MovieDetailPoster(
    posterUrl: String?,
    onClick: (() -> Unit)? = null,
    showPlayButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    val description = if (posterUrl.isNullOrBlank()) {
        "Movie poster placeholder"
    } else {
        "Movie poster"
    }

    val shape = RoundedCornerShape(MovieMateShapes.cardRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MovieMateColors.ScreenBackgroundGlow,
                        MovieMateColors.PosterPlaceholder,
                        MovieMateColors.AccentContainer
                    )
                )
            )
            .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = MovieMateIcons.image,
            style = MaterialTheme.typography.titleMedium,
            color = MovieMateColors.PrimaryText
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(MovieMateSpacing.detailHeroOverlayHeight)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MovieMateColors.ScreenBackground.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        if (showPlayButton) {
            Box(
                modifier = Modifier
                    .size(MovieMateSpacing.detailPlayButtonSize)
                    .background(
                        color = MovieMateColors.SoftControl.copy(alpha = 0.78f),
                        shape = CircleShape
                    )
                    .border(
                        width = MovieMateSpacing.detailPlayButtonBorderWidth,
                        color = MovieMateColors.PrimaryText.copy(alpha = 0.82f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = MovieMateIcons.provider,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MovieMateColors.PrimaryText
                )
            }
        }
    }
}
