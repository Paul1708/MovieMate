package de.fh.erfurt.moviemate.presentation.movies.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import de.fh.erfurt.moviemate.presentation.components.DeleteBadge
import de.fh.erfurt.moviemate.presentation.components.DeleteConfirmationDialog
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.components.ScreenEmptyState
import de.fh.erfurt.moviemate.presentation.components.ScreenErrorState
import de.fh.erfurt.moviemate.presentation.components.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.components.SearchField
import de.fh.erfurt.moviemate.presentation.components.deleteModeWiggle
import de.fh.erfurt.moviemate.presentation.movies.components.MovieCard
import de.fh.erfurt.moviemate.presentation.preview.previewHomeUiState
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Stateless Home screen. Data and actions are supplied by HomeRoute.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = state.isDeleteMode && state.pendingDeleteMovieId == null) {
        onAction(HomeUiAction.ExitDeleteMode)
    }

    MovieMateScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MovieMateScreenDefaults.HorizontalPadding)
                .padding(top = MovieMateScreenDefaults.VerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeTopBar(
                selectedTab = state.selectedTab,
                onTabSelected = { onAction(HomeUiAction.TabSelected(it)) },
                onSearchClicked = { onAction(HomeUiAction.SearchClicked) },
                onFilterClicked = { onAction(HomeUiAction.FilterClicked) }
            )

            if (state.isSearchVisible) {
                Spacer(modifier = Modifier.height(MovieMateSpacing.fieldSpacing))
                SearchField(
                    value = state.searchQuery,
                    onValueChange = { onAction(HomeUiAction.SearchChanged(it)) },
                    placeholder = "Find a movie",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(MovieMateScreenDefaults.ContentTopSpacing))

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
                    verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.itemSpacing),
                    contentPadding = PaddingValues(
                        bottom = MovieMateSpacing.navigationContentBottomPadding
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = MovieMateSpacing.listTopPadding)
                ) {
                    items(state.movies, key = { it.id }) { movie ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .deleteModeWiggle(state.isDeleteMode)
                        ) {
                            MovieCard(
                                movie = movie,
                                onClick = {
                                    if (state.isDeleteMode) {
                                        onAction(HomeUiAction.ExitDeleteMode)
                                    } else {
                                        onAction(HomeUiAction.MovieClicked(movie.id))
                                    }
                                },
                                onLongClick = { onAction(HomeUiAction.MovieLongPressed(movie.id)) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (state.isDeleteMode) {
                                DeleteBadge(
                                    contentDescription = "Delete movie ${movie.title}",
                                    onClick = { onAction(HomeUiAction.DeleteMovieClicked(movie.id)) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(
                                            top = MovieMateSpacing.compactSpacing,
                                            end = MovieMateSpacing.compactSpacing
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!state.isDeleteMode) {
            FloatingActionButton(
                onClick = { onAction(HomeUiAction.AddClicked) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = MovieMateScreenDefaults.HorizontalPadding,
                        bottom = MovieMateSpacing.detailLargeSpacing
                    ),
                shape = CircleShape,
                containerColor = MovieMateColors.Accent,
                contentColor = MovieMateColors.PrimaryText
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (state.isFilterDialogVisible) {
        HomeFilterDialog(
            categories = state.availableCategories,
            selectedCategoryId = state.selectedCategoryId,
            selectedSortBy = state.selectedSortBy,
            onCategorySelected = { onAction(HomeUiAction.CategorySelected(it)) },
            onSortSelected = { onAction(HomeUiAction.SortSelected(it)) },
            onClearClicked = { onAction(HomeUiAction.ClearCategoryFilterClicked) },
            onClearSortClicked = { onAction(HomeUiAction.ClearSortClicked) },
            onDismiss = { onAction(HomeUiAction.FilterDismissed) }
        )
    }

    val pendingDeleteMovie = state.movies.firstOrNull { movie ->
        movie.id == state.pendingDeleteMovieId
    }
    if (pendingDeleteMovie != null) {
        DeleteConfirmationDialog(
            title = "Delete movie?",
            message = "Are you sure you want to delete \"${pendingDeleteMovie.title}\"? This action cannot be undone.",
            onConfirm = { onAction(HomeUiAction.ConfirmDeleteMovieClicked) },
            onDismiss = { onAction(HomeUiAction.DeleteMovieDismissed) }
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
