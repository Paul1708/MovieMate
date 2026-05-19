package de.fh.erfurt.moviemate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.presentation.MovieMateApp

// Einstiegspunkt der Android-App.
@Composable
@Preview
fun App() {
    MaterialTheme {
        MovieMateApp()
    }
}
