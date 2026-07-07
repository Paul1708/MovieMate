package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcon
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIconType
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Einheitliche Icon-Schaltfläche für Topbars.
 *
 * Home- und Kategorie-Screen nutzen diese Composable gemeinsam, damit die
 * Buttons beim Screenwechsel dieselbe Größe, Höhe und Ausrichtung behalten.
 */
@Composable
fun TopBarIconButton(
    text: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopBarIconButtonContainer(
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MovieMateColors.PrimaryText
        )
    }
}

@Composable
fun TopBarIconButton(
    icon: MovieMateIconType,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopBarIconButtonContainer(
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = modifier
    ) {
        MovieMateIcon(
            icon = icon,
            tint = MovieMateColors.PrimaryText,
            modifier = Modifier.size(MovieMateSpacing.topBarGlyphSize)
        )
    }
}

@Composable
private fun TopBarIconButtonContainer(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(MovieMateSpacing.topBarIconSize)
            .background(MovieMateColors.SoftControl, CircleShape)
            .border(
                width = MovieMateSpacing.dividerThickness,
                color = MovieMateColors.SoftControlBorder,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
