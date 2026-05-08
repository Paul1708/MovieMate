package de.fh.erfurt.moviemate

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.presentation.HomeScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        HomeScreen()
    }
}