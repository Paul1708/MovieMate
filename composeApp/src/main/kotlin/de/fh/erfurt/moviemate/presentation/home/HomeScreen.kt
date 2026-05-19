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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.presentation.MovieCard
import de.fh.erfurt.moviemate.presentation.MovieMateColors
import de.fh.erfurt.moviemate.presentation.ScreenEmptyState
import de.fh.erfurt.moviemate.presentation.ScreenErrorState
import de.fh.erfurt.moviemate.presentation.ScreenHeader
import de.fh.erfurt.moviemate.presentation.ScreenHeaderDefaults
import de.fh.erfurt.moviemate.presentation.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.SearchField

/**
 * Container fuer den Homescreen.
 *
 * Diese Funktion verbindet AndroidX ViewModel, lifecycle-aware State-Collection
 * und die stateless Rendering-Funktion.
 */
@Composable
fun HomeRoute(
    initialCategory: CategoryType? = null,
    modifier: Modifier = Modifier,
    onMovieSelected: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(initialCategory) {
        if (state.selectedCategory != initialCategory) {
            viewModel.onAction(HomeUiAction.CategorySelected(initialCategory))
        }
    }

    HomeScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
            if (action is HomeUiAction.MovieClicked) {
                onMovieSelected(action.movieId)
            }
        },
        modifier = modifier
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
    onAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MovieMateColors.ScreenBackground)
            .padding(
                horizontal = ScreenHeaderDefaults.HorizontalPadding,
                vertical = ScreenHeaderDefaults.VerticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenHeader(
            title = "Home",
            topBar = {
                HomeTopBar(
                    selectedTab = state.selectedTab,
                    onTabSelected = { onAction(HomeUiAction.TabSelected(it)) },
                    onSearchClicked = { onAction(HomeUiAction.SearchClicked) },
                    onFilterClicked = { onAction(HomeUiAction.FilterClicked) },
                    onSortClicked = { onAction(HomeUiAction.SortClicked) }
                )
            }
        )

        if (state.isSearchVisible) {
            Spacer(modifier = Modifier.height(10.dp))
            SearchField(
                value = state.searchQuery,
                onValueChange = { onAction(HomeUiAction.SearchChanged(it)) },
                placeholder = "Find a movie",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(ScreenHeaderDefaults.ContentTopSpacing))

        if (state.errorMessage != null) {
            ScreenErrorState(
                message = state.errorMessage,
                onRetryClicked = { onAction(HomeUiAction.RetryClicked) }
            )
        } else if (state.isLoading) {
            ScreenLoadingState(message = "Loading movies...")
        } else if (state.movies.isEmpty()) {
            ScreenEmptyState(message = "No movies found")
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
            selectedSortBy = state.selectedSortBy,
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
