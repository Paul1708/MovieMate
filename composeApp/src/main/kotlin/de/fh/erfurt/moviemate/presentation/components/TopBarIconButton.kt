package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

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
    Box(
        modifier = modifier
            .size(28.dp)
            .background(MovieMateColors.SoftControl, CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = MovieMateColors.SecondaryText
        )
    }
}
