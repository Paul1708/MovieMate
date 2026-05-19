package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.fh.erfurt.moviemate.presentation.theme.MovieMateColors
import de.fh.erfurt.moviemate.presentation.components.ScreenEmptyState
import de.fh.erfurt.moviemate.presentation.components.ScreenErrorState
import de.fh.erfurt.moviemate.presentation.components.ScreenHeader
import de.fh.erfurt.moviemate.presentation.components.ScreenHeaderDefaults
import de.fh.erfurt.moviemate.presentation.components.ScreenLoadingState
import de.fh.erfurt.moviemate.presentation.components.SearchField
import de.fh.erfurt.moviemate.presentation.components.TopBarIconButton
import de.fh.erfurt.moviemate.presentation.preview.previewCategoriesUiState

/**
 * Sichtbare Oberfläche des Kategorie-Screens.
 *
 * Der Screen zeigt Kategorien als Grid im Stil des Mockups. Er nutzt denselben
 * State/Action-Ansatz wie der Homescreen und ist dadurch später leicht an echte
 * Daten oder Navigation anzuschließen.
 */
@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    onAction: (CategoriesUiAction) -> Unit,
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
            title = "Categories",
            topBar = {
                CategoriesTopBar(
                    onSearchClicked = { onAction(CategoriesUiAction.SearchClicked) }
                )
            }
        )

        if (state.isSearchVisible) {
            Spacer(modifier = Modifier.height(10.dp))
            SearchField(
                value = state.searchQuery,
                onValueChange = { onAction(CategoriesUiAction.SearchChanged(it)) },
                placeholder = "Find a category",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(ScreenHeaderDefaults.ContentTopSpacing))

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

@Composable
private fun CategoriesTopBar(
    onSearchClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))

        TopBarIconButton(
            text = "⌕",
            contentDescription = "Search categories",
            onClick = onSearchClicked
        )
    }
}

/**
 * Grid für die Kategorie-Karten.
 *
 * Zwei Spalten entsprechen dem Mockup und funktionieren gut auf kleinen
 * Smartphone-Bildschirmen.
 */
@Composable
private fun CategoriesGrid(
    categories: List<CategoryUiModel>,
    onCategoryClicked: (CategoryUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(categories, key = { it.type.name }) { category ->
            CategoryFolderCard(
                category = category,
                onClick = { onCategoryClicked(category) }
            )
        }
    }
}

/**
 * Einzelne Kategorie-Karte im Folder-Stil.
 *
 * Der Aufbau imitiert die schwarzen Ordner aus dem Mockup, bleibt aber komplett
 * in Compose gezeichnet und braucht keine externen Bilddateien.
 */
@Composable
private fun CategoryFolderCard(
    category: CategoryUiModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Category ${category.label}" }
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(width = 82.dp, height = 58.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 14.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        if (category.movieCountText != null) {
            Text(
                text = category.movieCountText,
                style = MaterialTheme.typography.labelSmall,
                color = MovieMateColors.SecondaryText,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
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
