package de.fh.erfurt.moviemate.presentation.movies.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors

/**
 * Textabschnitt der Detailansicht, zum Beispiel Beschreibung oder Kommentar.
 */
@Composable
fun MovieDetailSection(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MovieMateColors.PrimaryText,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
