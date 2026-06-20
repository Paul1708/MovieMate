package de.fh.erfurt.moviemate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.app.di.AppContainer
import de.fh.erfurt.moviemate.presentation.app.MovieMateApp
import de.fh.erfurt.moviemate.presentation.theme.MovieMateTheme

/**
 * Einstiegspunkt der Android-App mit globalem MovieMate-Theme.
 */
@Composable
@Preview
fun App(
    appContainer: AppContainer? = null
) {
    MovieMateTheme {
        MovieMateApp(appContainer = appContainer)
    }
}
// test
