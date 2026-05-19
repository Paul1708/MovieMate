package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes

/**
 * Posterfläche der Detailansicht; im Edit-Modus als Auswahlfläche anklickbar.
 */
@Composable
fun MovieDetailPoster(
    posterUrl: String?,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val description = if (posterUrl.isNullOrBlank()) {
        "Movie poster placeholder"
    } else {
        "Movie poster"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.35f)
            .clip(RoundedCornerShape(MovieMateShapes.cardRadius))
            .background(MovieMateColors.PosterPlaceholder)
            .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = MovieMateIcons.image,
            style = MaterialTheme.typography.titleMedium,
            color = MovieMateColors.PrimaryText
        )
    }
}
