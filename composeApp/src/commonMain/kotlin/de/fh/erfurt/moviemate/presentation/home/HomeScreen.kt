package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.BottomBar
import de.fh.erfurt.moviemate.presentation.MovieCard

// Container für den Homescreen, solange noch kein echtes ViewModel angebunden ist.
// Er hält lokalen UI-State und filtert die Mockdaten direkt in der UI-Schicht.
@Composable
fun HomeRouteMock() {
    var selectedTab by remember { mutableStateOf(HomeTab.All) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    val movies = remember { previewHomeMovies() }

    // Platzhalter-Filterung, bis die Filme später über GetMoviesUseCase geladen werden.
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

// Reine Homescreen-UI. Alle Daten und Nutzeraktionen kommen über Parameter hinein.
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
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeTab.entries.forEach { tab ->
                    Text(
                        text = tab.label,
                        fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 4.dp)
                    )
                }

                Row {
                    Button(onClick = onSearchClicked) {
                        Text("Search")
                    }
                    Button(onClick = onFilterClicked) {
                        Text("Filter")
                    }
                }
            }

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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.movies, key = { it.id }) { movie ->
                        Box(
                            modifier = Modifier.clickable {
                                onMovieClicked(movie.id)
                            }
                        ) {
                            MovieCard(
                                title = movie.title,
                                year = movie.yearText,
                                time = movie.runtimeText,
                                category = movie.categoryText,
                                isWatched = movie.isWatched,
                                modifier = Modifier,
                            )
                        }
                    }
                }
            }
        }
    }
}
