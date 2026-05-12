package de.fh.erfurt.moviemate.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.fh.erfurt.moviemate.presentation.categories.CategoriesRouteMock
import de.fh.erfurt.moviemate.presentation.home.HomeRouteMock
import de.fh.erfurt.moviemate.presentation.mock.MovieMateMockStore

/**
 * Gemeinsamer App-Rahmen für die aktuelle Mock-UI.
 *
 * Diese Composable hält die aktive BottomBar-Auswahl und zeigt den passenden
 * Screen an. Später kann diese einfache Logik durch echte Navigation ersetzt
 * werden.
 */
@Composable
fun MovieMateAppMock() {
    val mockStore = remember { MovieMateMockStore() }
    var destination by remember { mutableStateOf<MovieMateDestination>(MovieMateDestination.Home()) }

    Scaffold(
        containerColor = MovieMateColors.ScreenBackground,
        bottomBar = {
            BottomBar(
                selectedItem = destination.selectedBottomNavItem(),
                onItemSelected = { destination = it.toDestination() }
            )
        }
    ) { paddingValues ->
        val contentModifier = Modifier.padding(paddingValues)

        when (val currentDestination = destination) {
            is MovieMateDestination.Home -> HomeRouteMock(
                movies = mockStore.movies,
                initialCategory = currentDestination.initialCategory,
                modifier = contentModifier,
                onMovieSelected = { movieId ->
                    destination = MovieMateDestination.Detail(movieId)
                }
            )

            MovieMateDestination.Categories -> CategoriesRouteMock(
                movies = mockStore.movies,
                modifier = contentModifier,
                onCategorySelected = { category ->
                    destination = MovieMateDestination.Home(initialCategory = category)
                }
            )

            is MovieMateDestination.Detail -> PlaceholderScreen(
                title = "Details",
                message = "Detail-Screen für ${currentDestination.movieId}",
                modifier = contentModifier
            )

            MovieMateDestination.Add -> PlaceholderScreen(
                title = "Add",
                message = "Add-Screen wird vorbereitet.",
                modifier = contentModifier
            )

            MovieMateDestination.Backup -> PlaceholderScreen(
                title = "Backup",
                message = "Backup-Screen wird vorbereitet.",
                modifier = contentModifier
            )
        }
    }
}
