package de.fh.erfurt.moviemate.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Ladezustand für Screens, die Daten abrufen.
 */
@Composable
fun ScreenLoadingState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MovieMateSpacing.stateTopSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = MovieMateSpacing.titleSpacing)
        )
    }
}

/**
 * Leerzustand für Screens ohne anzeigbare Daten.
 */
@Composable
fun ScreenEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MovieMateSpacing.stateTopSpacing)
    )
}

/**
 * Fehlerzustand mit Retry-Aktion für erneut ladbare Inhalte.
 */
@Composable
fun ScreenErrorState(
    message: String,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MovieMateSpacing.stateTopSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.titleSpacing)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetryClicked) {
            Text("Retry")
        }
    }
}
