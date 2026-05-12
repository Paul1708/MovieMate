package de.fh.erfurt.moviemate.presentation.home

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
import androidx.compose.ui.unit.dp

/**
 * Ladezustand des Homescreens.
 *
 * Dieser Baustein wird angezeigt, wenn später Filme aus Repository, Datenbank
 * oder Netzwerk geladen werden. Aktuell ist er vor allem für Preview und
 * Struktur vorbereitet.
 */
@Composable
fun HomeLoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = "Loading movies...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

/**
 * Leerzustand des Homescreens.
 *
 * Dieser Baustein wird genutzt, wenn keine Filme vorhanden sind oder Suche und
 * Filter keine Treffer liefern.
 */
@Composable
fun HomeEmptyState(
    modifier: Modifier = Modifier
) {
    Text(
        text = "No movies found",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
    )
}

/**
 * Fehlerzustand des Homescreens.
 *
 * Dieser Baustein zeigt später Backend- oder Datenbankfehler an und bietet
 * schon einen Retry-Button an, der an `RetryClicked` gebunden ist.
 */
@Composable
fun HomeErrorState(
    message: String,
    onRetryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
