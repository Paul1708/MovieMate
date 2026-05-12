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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.BottomBar
import de.fh.erfurt.moviemate.presentation.MovieCard

/**
 * Einstieg für den Homescreen während der UI-Entwicklung.
 *
 * Diese Funktion erzeugt aktuell ein einfaches `HomeViewModel` mit Mockdaten.
 * Später kann hier ein echtes ViewModel mit UseCases und Repository eingesetzt
 * werden, ohne die sichtbare UI stark umzubauen.
 */
@Composable
fun HomeRouteMock() {
    val viewModel = remember { HomeViewModel() }

    HomeScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
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
    onAction: (HomeUiAction) -> Unit
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
                onTabSelected = { onAction(HomeUiAction.TabSelected(it)) },
                onSearchClicked = { onAction(HomeUiAction.SearchClicked) },
                onFilterClicked = { onAction(HomeUiAction.FilterClicked) }
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            if (state.isSearchVisible) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onAction(HomeUiAction.SearchChanged(it)) },
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
                            onClick = { onAction(HomeUiAction.MovieClicked(movie.id)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    if (state.isFilterDialogVisible) {
        HomeFilterDialog(
            selectedCategory = state.selectedCategory,
            selectedSortCriterion = state.selectedSortCriterion,
            onCategorySelected = { onAction(HomeUiAction.CategorySelected(it)) },
            onSortSelected = { onAction(HomeUiAction.SortSelected(it)) },
            onClearClicked = { onAction(HomeUiAction.ClearFiltersClicked) },
            onDismiss = { onAction(HomeUiAction.FilterDismissed) }
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = previewHomeUiState(),
        onAction = {}
    )
}
