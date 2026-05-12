package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.BottomBar
import de.fh.erfurt.moviemate.presentation.MovieCard

/**
 * Einstieg für den Homescreen während der UI-Entwicklung.
 *
 * Diese Funktion ersetzt vorübergehend ein echtes ViewModel. Sie merkt sich,
 * welcher Tab aktiv ist, ob die Suche sichtbar ist und welcher Suchtext
 * eingegeben wurde. Dadurch kann man den Homescreen schon ausprobieren,
 * obwohl Backend und Datenbank noch nicht angebunden sind.
 */
@Composable
fun HomeRouteMock() {
    var selectedTab by remember { mutableStateOf(HomeTab.All) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val movies = remember { previewHomeMovies() }

    // Filtert die Beispiel-Filme so, wie es später der GetMoviesUseCase machen soll.
    val filteredMovies = movies
        .filter { movie ->
            when (selectedTab) {
                HomeTab.All -> true
                HomeTab.Watched -> movie.isWatched
                HomeTab.WatchLater -> !movie.isWatched
            }
        }
        .filter { movie ->
            searchQuery.isBlank() || movie.title.contains(searchQuery, ignoreCase = true)
        }

    HomeScreen(
        state = HomeUiState(
            movies = filteredMovies,
            selectedTab = selectedTab,
            searchQuery = searchQuery,
            isSearchVisible = isSearchVisible
        ),
        onTabSelected = { selectedTab = it },
        onSearchQueryChanged = { searchQuery = it },
        onSearchClicked = { isSearchVisible = !isSearchVisible },
        onFilterClicked = {},
        onMovieClicked = {}
    )
}

/**
 * Sichtbare Oberfläche des Homescreens.
 *
 * Diese Funktion baut den Screen aus dem Mockup zusammen: Titel, Tab-Leiste,
 * Suchfeld, Filmliste und untere Navigation. Sie enthält selbst keine
 * Backend-Logik, sondern bekommt alle Daten über den State und meldet Klicks
 * über Callbacks nach außen.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onTabSelected: (HomeTab) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onMovieClicked: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            HomeTopBar(
                selectedTab = state.selectedTab,
                onTabSelected = onTabSelected,
                onSearchClicked = onSearchClicked,
                onFilterClicked = onFilterClicked
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            if (state.isSearchVisible) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Find your movie") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.errorMessage != null) {
                Text(state.errorMessage)
            } else if (state.isLoading) {
                Text("Loading movies...")
            } else if (state.movies.isEmpty()) {
                Text("No movies found")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp)
                ) {
                    items(state.movies, key = { it.id }) { movie ->
                        MovieCard(
                            title = movie.title,
                            year = movie.yearText,
                            time = movie.runtimeText,
                            category = movie.categoryText,
                            rating = movie.ratingText,
                            isWatched = movie.isWatched,
                            onClick = { onMovieClicked(movie.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
