package de.fh.erfurt.moviemate.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.BottomBar
import de.fh.erfurt.moviemate.presentation.MovieCard
import de.fh.erfurt.moviemate.presentation.MovieMateColors

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
        containerColor = MovieMateColors.ScreenBackground,
        bottomBar = {
            BottomBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MovieMateColors.ScreenBackground)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.titleMedium,
                color = MovieMateColors.PrimaryText
            )

            Spacer(modifier = Modifier.height(10.dp))

            HomeTopBar(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(HomeUiAction.TabSelected(it)) },
                onSearchClicked = { onAction(HomeUiAction.SearchClicked) },
                onFilterClicked = { onAction(HomeUiAction.FilterClicked) },
                onSortClicked = { onAction(HomeUiAction.SortClicked) }
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp,
                color = MovieMateColors.Divider
            )

            if (state.isSearchVisible) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onAction(HomeUiAction.SearchChanged(it)) },
                    placeholder = { Text("Find your movie") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MovieMateColors.CardBackground,
                        unfocusedContainerColor = MovieMateColors.CardBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (state.errorMessage != null) {
                HomeErrorState(
                    message = state.errorMessage,
                    onRetryClicked = { onAction(HomeUiAction.RetryClicked) }
                )
            } else if (state.isLoading) {
                HomeLoadingState()
            } else if (state.movies.isEmpty()) {
                HomeEmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 2.dp)
                ) {
                    items(state.movies, key = { it.id }) { movie ->
                        MovieCard(
                            movie = movie,
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
            onCategorySelected = { onAction(HomeUiAction.CategorySelected(it)) },
            onClearClicked = { onAction(HomeUiAction.ClearCategoryFilterClicked) },
            onDismiss = { onAction(HomeUiAction.FilterDismissed) }
        )
    }

    if (state.isSortDialogVisible) {
        HomeSortDialog(
            selectedSortCriterion = state.selectedSortCriterion,
            onSortSelected = { onAction(HomeUiAction.SortSelected(it)) },
            onClearClicked = { onAction(HomeUiAction.ClearSortClicked) },
            onDismiss = { onAction(HomeUiAction.SortDismissed) }
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

@Preview
@Composable
private fun HomeScreenEmptyPreview() {
    HomeScreen(
        state = previewHomeUiState().copy(movies = emptyList()),
        onAction = {}
    )
}

@Preview
@Composable
private fun HomeScreenLoadingPreview() {
    HomeScreen(
        state = previewHomeUiState().copy(isLoading = true),
        onAction = {}
    )
}

@Preview
@Composable
private fun HomeScreenErrorPreview() {
    HomeScreen(
        state = previewHomeUiState().copy(
            movies = emptyList(),
            errorMessage = "Movies could not be loaded."
        ),
        onAction = {}
    )
}
