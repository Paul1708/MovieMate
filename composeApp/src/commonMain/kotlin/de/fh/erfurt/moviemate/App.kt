package de.fh.erfurt.moviemate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.presentation.MovieMateAppMock

// Einstiegspunkt der Compose-App. Aktuell wird hier die Mock-Navigation angezeigt.
@Composable
@Preview
fun App() {
    MaterialTheme {
        MovieMateAppMock()
    }
}
