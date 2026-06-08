package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenBackground
import de.fh.erfurt.moviemate.presentation.components.ScreenEmptyState
import de.fh.erfurt.moviemate.presentation.components.ScreenErrorState
import de.fh.erfurt.moviemate.presentation.components.MovieMateScreenDefaults
import de.fh.erfurt.moviemate.presentation.components.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.components.SearchField
import de.fh.erfurt.moviemate.presentation.components.TopBarIconButton
import de.fh.erfurt.moviemate.presentation.preview.previewCategoriesUiState
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.theme.MovieMateIcons
import de.fh.erfurt.moviemate.presentation.theme.MovieMateShapes
import de.fh.erfurt.moviemate.presentation.theme.MovieMateSpacing

/**
 * Category overview screen.
 *
 * The screen is stateless from the UI perspective: it renders the given state
 * and reports user interactions as actions so the current preview holder can
 * later be replaced by a real shared ViewModel.
 */
@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    onAction: (CategoriesUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    MovieMateScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = MovieMateScreenDefaults.HorizontalPadding
                )
                .padding(top = MovieMateScreenDefaults.VerticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoriesTopBar(
                onSearchClicked = { onAction(CategoriesUiAction.SearchClicked) }
            )

            if (state.isSearchVisible) {
                Spacer(modifier = Modifier.height(MovieMateSpacing.fieldSpacing))
                SearchField(
                    value = state.searchQuery,
                    onValueChange = { onAction(CategoriesUiAction.SearchChanged(it)) },
                    placeholder = "Find a category",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(MovieMateScreenDefaults.ContentTopSpacing))

            when {
                state.errorMessage != null -> ScreenErrorState(
                    message = state.errorMessage,
                    onRetryClicked = { onAction(CategoriesUiAction.RetryClicked) }
                )

                state.isLoading -> ScreenLoadingState(message = "Loading categories...")
                state.categories.isEmpty() -> ScreenEmptyState(message = "No categories found")
                else -> CategoriesGrid(
                    categories = state.categories,
                    onCategoryClicked = { onAction(CategoriesUiAction.CategoryClicked(it.type)) }
                )
            }
        }
    }
}

@Composable
private fun CategoriesTopBar(
    onSearchClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.headlineSmall,
                color = MovieMateColors.PrimaryText,
                fontWeight = FontWeight.Bold
            )

            TopBarIconButton(
                text = MovieMateIcons.search,
                contentDescription = "Search categories",
                onClick = onSearchClicked
            )
        }

        Box(
            modifier = Modifier
                .padding(top = MovieMateSpacing.titleSpacing)
                .fillMaxWidth()
                .height(MovieMateSpacing.categoryHeaderIndicatorHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MovieMateSpacing.categoryHeaderDividerHeight)
                    .align(Alignment.BottomStart)
                    .background(MovieMateColors.Divider)
            )
            Box(
                modifier = Modifier
                    .width(MovieMateSpacing.categoryHeaderIndicatorWidth)
                    .height(MovieMateSpacing.categoryHeaderIndicatorHeight)
                    .clip(RoundedCornerShape(MovieMateSpacing.categoryHeaderIndicatorHeight))
                    .background(MovieMateColors.AccentLight)
            )
        }
    }
}

@Composable
private fun CategoriesGrid(
    categories: List<CategoryUiModel>,
    onCategoryClicked: (CategoryUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(MovieMateSpacing.categoryGridSpacing),
        horizontalArrangement = Arrangement.spacedBy(MovieMateSpacing.categoryGridSpacing),
        contentPadding = PaddingValues(bottom = MovieMateSpacing.navigationContentBottomPadding),
        modifier = Modifier.fillMaxSize()
    ) {
        items(categories, key = { it.type.name }) { category ->
            CategoryTile(
                category = category,
                onClick = { onCategoryClicked(category) }
            )
        }
    }
}

@Composable
private fun CategoryTile(
    category: CategoryUiModel,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(MovieMateShapes.folderRadius)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(MovieMateSpacing.categoryTileHeight)
            .clip(shape)
            .background(MovieMateColors.CardBackgroundElevated)
            .border(
                width = MovieMateSpacing.dividerThickness,
                color = MovieMateColors.CardBorder,
                shape = shape
            )
            .semantics { contentDescription = "Category ${category.label}" }
            .clickable(onClick = onClick)
            .padding(MovieMateSpacing.categoryTilePadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CategoryGenreIcon(
            type = category.type,
            modifier = Modifier.size(MovieMateSpacing.categoryIconSize)
        )

        Spacer(modifier = Modifier.height(MovieMateSpacing.categoryIconBottomSpacing))

        Text(
            text = category.label,
            style = MaterialTheme.typography.labelMedium,
            color = MovieMateColors.PrimaryText,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = category.movieCountText ?: "0 Movies",
            style = MaterialTheme.typography.labelSmall,
            color = MovieMateColors.SecondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun CategoriesScreenPreview() {
    CategoriesScreen(
        state = previewCategoriesUiState(),
        onAction = {}
    )
}

@Preview
@Composable
private fun CategoriesScreenEmptyPreview() {
    CategoriesScreen(
        state = previewCategoriesUiState().copy(categories = emptyList()),
        onAction = {}
    )
}
